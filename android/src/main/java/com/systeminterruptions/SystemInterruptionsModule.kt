package com.systeminterruptions
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.os.Build
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
class SystemInterruptionsModule(reactContext: ReactApplicationContext) :
  NativeSystemInterruptionsSpec(reactContext) {
  private lateinit var audioManager: AudioManager
  private var lastOtherAudio = false
  private var lastCall = "none"
  private var modeListener: AudioManager.OnModeChangedListener? = null
  @RequiresApi(Build.VERSION_CODES.O)
  inner class PlaybackCallback : AudioManager.AudioPlaybackCallback() {
    override fun onPlaybackConfigChanged(configs: List<AudioPlaybackConfiguration>) {
      onPlayersChanged( configs)
    }
  }

  @delegate:RequiresApi(Build.VERSION_CODES.O)
  private val playbackCallback by lazy {
    PlaybackCallback()
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun onPlayersChanged(configs: List<AudioPlaybackConfiguration>) {
    val otherAudio = configs.any { isOtherAudio(it.audioAttributes) }
    if (otherAudio != lastOtherAudio) {
      lastOtherAudio = otherAudio
      emitOnOtherAudio(if (otherAudio) "started" else "stopped")
    }
  }

  // TODO: handle screening and redirect modes. For now they are ignored, so NORMAL after them is not reported as "ended".
  private fun callFromMode(mode: Int): String? = when (mode) {
    AudioManager.MODE_RINGTONE -> "ringing"
    AudioManager.MODE_IN_CALL,
    AudioManager.MODE_IN_COMMUNICATION -> "connected"
    AudioManager.MODE_NORMAL -> "none"
    else -> null
  }

  private fun onModeChanged(mode: Int) {
    val call = callFromMode(mode) ?: return
    if (call == lastCall) return
    lastCall = call
    emitOnCall(if (call == "none") "ended" else call)
  }
  override fun startObservingAsync(promise: Promise) {
    if (Build.VERSION.SDK_INT <26) {
      promise.reject("ERR_UNSUPPORTED", "audio playback callback requires Android API 26+")
      return;
    }
    audioManager = reactApplicationContext.getSystemService(AudioManager::class.java)
    try {
      audioManager.registerAudioPlaybackCallback(playbackCallback, null)
      lastOtherAudio = audioManager.activePlaybackConfigurations.any { isOtherAudio(it.audioAttributes) }
      lastCall = callFromMode(audioManager.mode) ?: "none"
      if (Build.VERSION.SDK_INT >= 31) {
        val listener = AudioManager.OnModeChangedListener { mode ->
          onModeChanged(mode)
        }
        audioManager.addOnModeChangedListener(reactApplicationContext.mainExecutor, listener)
        modeListener = listener
      }
    } catch (e: Exception) {
      audioManager.unregisterAudioPlaybackCallback(playbackCallback)
      promise.reject("ERR_START_OBSERVING", e)
      return
    }
    promise.resolve(null)
  }

  override fun stopObservingAsync(promise: Promise) {
    if (Build.VERSION.SDK_INT <26) {
      promise.reject("ERR_UNSUPPORTED", "audio playback callback requires Android API 26+")
      return;
    }
    try {


      audioManager.unregisterAudioPlaybackCallback(playbackCallback)
      if (Build.VERSION.SDK_INT >= 31) {
        modeListener?.let { audioManager.removeOnModeChangedListener(it) }
      }
      promise.resolve(null);
      return;
    }catch (e: Exception) {
      promise.reject("ERR_STOP_OBSERVING", e)
    }
  }

  override fun isInCallAsync(promise: Promise) {
    try {
      val am = reactApplicationContext.getSystemService(AudioManager::class.java)
      if (Build.VERSION.SDK_INT <31) {
        promise.reject("ERR_UNSUPPORTED", "isInCallAsync requires Android API 31+")
        return;
      }
      promise.resolve(callFromMode(am.mode) == "connected")
    }catch (e: Exception) {
      promise.reject("ERR_IS_IN_CALL", e)
      return
    }
  }

  override fun getAudioRouteAsync(promise: Promise) {
    TODO("Not yet implemented")
  }

  override fun isOtherAudioPlayingAsync(promise: Promise) {
    try {
      if (Build.VERSION.SDK_INT < 26) {
        promise.reject("ERR_UNSUPPORTED", "isOtherAudioPlayingAsync requires Android API 26+")
        return
      }
      val am = reactApplicationContext.getSystemService(AudioManager::class.java)
      promise.resolve(am.activePlaybackConfigurations.any { isOtherAudio(it.audioAttributes) })
    } catch (e: Exception) {
      promise.reject("ERR_IS_OTHER_AUDIO_PLAYING", e)
    }
  }
  private fun isOtherAudio(attrs: AudioAttributes): Boolean {
    if (attrs.usage == AudioAttributes.USAGE_VOICE_COMMUNICATION) return false
    return when (attrs.contentType) {
      AudioAttributes.CONTENT_TYPE_MUSIC,
      AudioAttributes.CONTENT_TYPE_MOVIE,
      AudioAttributes.CONTENT_TYPE_SPEECH -> true
      AudioAttributes.CONTENT_TYPE_UNKNOWN -> attrs.usage == AudioAttributes.USAGE_MEDIA
      else -> false
    }
  }

  companion object {
    const val NAME = NativeSystemInterruptionsSpec.NAME
  }
}
