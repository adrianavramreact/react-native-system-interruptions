package com.systeminterruptions
import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.os.Build
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
class SystemInterruptionsModule(reactContext: ReactApplicationContext) :
  NativeSystemInterruptionsSpec(reactContext) {
  private val audioManager by lazy {
    reactApplicationContext.getSystemService(AudioManager::class.java)
  }
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
    val otherAudio = configs.any { isOtherAudio(it.audioAttributes.usage, it.audioAttributes.contentType) }
    if (otherAudio != lastOtherAudio) {
      lastOtherAudio = otherAudio
      emitOnOtherAudio(if (otherAudio) "started" else "stopped")
    }
  }

  private fun onModeChanged(mode: Int) {
    val call = callFromMode(mode) ?: return
    if (call == lastCall) return
    lastCall = call
    emitOnCall(if (call == "none") "ended" else call)
  }
  @RequiresApi(Build.VERSION_CODES.O)
  private fun startOtherAudio() {
    audioManager.registerAudioPlaybackCallback(playbackCallback, null)
    lastOtherAudio = audioManager.activePlaybackConfigurations.any { isOtherAudio(it.audioAttributes.usage, it.audioAttributes.contentType) }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun stopOtherAudio() {
    audioManager.unregisterAudioPlaybackCallback(playbackCallback)
  }

  @RequiresApi(Build.VERSION_CODES.S)
  private fun startCall() {
    lastCall = callFromMode(audioManager.mode) ?: "none"
    val listener = AudioManager.OnModeChangedListener { mode ->
      onModeChanged(mode)
    }
    audioManager.addOnModeChangedListener(reactApplicationContext.mainExecutor, listener)
    modeListener = listener
  }

  @RequiresApi(Build.VERSION_CODES.S)
  private fun stopCall() {
    modeListener?.let { audioManager.removeOnModeChangedListener(it) }
    modeListener = null
  }

  // API levels are checked by isEventSupported before each call.
  @SuppressLint("NewApi")
  override fun startObservingAsync(event: String, promise: Promise) {
    if (!isEventSupported(event, Build.VERSION.SDK_INT)) {
      promise.reject("ERR_UNSUPPORTED", "$event is not supported on this Android version")
      return
    }
    try {
      when (event) {
        EVENT_OTHER_AUDIO -> startOtherAudio()
        EVENT_CALL -> startCall()
        // TODO: register AudioDeviceCallback for audioRoute.
      }
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("ERR_START_OBSERVING", e)
    }
  }

  @SuppressLint("NewApi")
  override fun stopObservingAsync(event: String, promise: Promise) {
    if (!isEventSupported(event, Build.VERSION.SDK_INT)) {
      promise.resolve(null)
      return
    }
    try {
      when (event) {
        EVENT_OTHER_AUDIO -> stopOtherAudio()
        EVENT_CALL -> stopCall()
      }
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("ERR_STOP_OBSERVING", e)
    }
  }

  override fun isAvailableAsync(event: String, promise: Promise) {
    promise.resolve(isEventSupported(event, Build.VERSION.SDK_INT))
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
        promise.resolve(am.activePlaybackConfigurations.any { isOtherAudio(it.audioAttributes.usage, it.audioAttributes.contentType) })
    } catch (e: Exception) {
      promise.reject("ERR_IS_OTHER_AUDIO_PLAYING", e)
    }
  }
  companion object {
    const val NAME = NativeSystemInterruptionsSpec.NAME
  }
}
