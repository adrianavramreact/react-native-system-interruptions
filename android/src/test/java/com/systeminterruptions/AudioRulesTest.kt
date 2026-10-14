package com.systeminterruptions
import android.media.AudioAttributes
import android.media.AudioManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AudioRulesTest {
  @Test
  fun callFromMode_ringtone_isRinging() {
    assertEquals("ringing", callFromMode(AudioManager.MODE_RINGTONE))
  }

  @Test
  fun callFromMode_cellularAndVoip_areConnected() {
    assertEquals("connected", callFromMode(AudioManager.MODE_IN_CALL))
    assertEquals("connected", callFromMode(AudioManager.MODE_IN_COMMUNICATION))
  }

  @Test
  fun callFromMode_normal_isNone() {
    assertEquals("none", callFromMode(AudioManager.MODE_NORMAL))
  }

  @Test
  fun callFromMode_screening_isIgnored() {
    assertNull(callFromMode(AudioManager.MODE_CALL_SCREENING))
  }

  @Test
  fun isOtherAudio_music_isTrue() {
    assertTrue(isOtherAudio(AudioAttributes.USAGE_MEDIA, AudioAttributes.CONTENT_TYPE_MUSIC))
  }

  @Test
  fun isOtherAudio_spotifyUnknownMedia_isTrue() {
    assertTrue(isOtherAudio(AudioAttributes.USAGE_MEDIA, AudioAttributes.CONTENT_TYPE_UNKNOWN))
  }

  @Test
  fun isOtherAudio_voiceCall_isFalse() {
    assertFalse(isOtherAudio(AudioAttributes.USAGE_VOICE_COMMUNICATION, AudioAttributes.CONTENT_TYPE_SPEECH))
  }

  @Test
  fun isOtherAudio_notification_isFalse() {
    assertFalse(isOtherAudio(AudioAttributes.USAGE_NOTIFICATION, AudioAttributes.CONTENT_TYPE_SONIFICATION))
  }
}
