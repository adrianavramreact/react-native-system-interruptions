package com.systeminterruptions
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build

internal const val EVENT_CALL = "call"
internal const val EVENT_OTHER_AUDIO = "otherAudio"
internal const val EVENT_AUDIO_ROUTE = "audioRoute"

// audioInterruption is never emitted on Android (no audio focus), so it is unsupported.
internal fun isEventSupported(event: String, sdkInt: Int): Boolean = when (event) {
  EVENT_CALL -> sdkInt >= Build.VERSION_CODES.S
  EVENT_OTHER_AUDIO -> sdkInt >= Build.VERSION_CODES.O
  EVENT_AUDIO_ROUTE -> sdkInt >= Build.VERSION_CODES.M
  else -> false
}

// TODO: handle screening and redirect modes. For now they are ignored, so NORMAL after them is not reported as "ended".
internal fun callFromMode(mode: Int): String? = when (mode) {
  AudioManager.MODE_RINGTONE -> "ringing"
  AudioManager.MODE_IN_CALL,
  AudioManager.MODE_IN_COMMUNICATION -> "connected"
  AudioManager.MODE_NORMAL -> "none"
  else -> null
}

internal fun isOtherAudio(usage: Int, contentType: Int): Boolean {
  if (usage == AudioAttributes.USAGE_VOICE_COMMUNICATION) return false
  return when (contentType) {
    AudioAttributes.CONTENT_TYPE_MUSIC,
    AudioAttributes.CONTENT_TYPE_MOVIE,
    AudioAttributes.CONTENT_TYPE_SPEECH -> true
    AudioAttributes.CONTENT_TYPE_UNKNOWN -> usage == AudioAttributes.USAGE_MEDIA
    else -> false
  }
}
