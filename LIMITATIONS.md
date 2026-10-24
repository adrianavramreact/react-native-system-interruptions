# Limitations

Known platform limits of `react-native-system-interruptions`. See `docs/DESIGN.md` for how each event is detected.

## iOS

- **`otherAudio` is foreground only.** In the background the app is suspended, and the `audio` background mode is only allowed for real playback ([App Review 2.5.4](https://developer.apple.com/app-store/review/guidelines/#2.5.4)). The module never activates the audio session, so there is no notification; the value is read with [`isOtherAudioPlaying`](https://developer.apple.com/documentation/avfaudio/avaudiosession/isotheraudioplaying) when the app becomes active.

## Android

- **Below API 31: no `call` events.** The mode listener ([`addOnModeChangedListener`](https://developer.android.com/reference/android/media/AudioManager#addOnModeChangedListener(java.util.concurrent.Executor,%20android.media.AudioManager.OnModeChangedListener))) needs API 31. `isInCallAsync()` rejects with code `ERR_UNSUPPORTED`.
- **Below API 26: no `otherAudio` events.** [`AudioPlaybackCallback`](https://developer.android.com/reference/android/media/AudioManager.AudioPlaybackCallback) and [`getActivePlaybackConfigurations`](https://developer.android.com/reference/android/media/AudioManager#getActivePlaybackConfigurations()) need API 26. `isOtherAudioPlayingAsync()` rejects with code `ERR_UNSUPPORTED`.
- **`audioRoute` reports only `bluetooth`, `wired` and `speaker`.** It works on API 24 and above. Other types are not reported. They can be added on request, after more testing on real devices.
- **`call` covers ringing, cellular and VoIP calls.** Call screening (`MODE_CALL_SCREENING`) and calls moved to another device (`MODE_CALL_REDIRECT`, `MODE_COMMUNICATION_REDIRECT`) are not reported yet. They can be added in a future release, or earlier on request.
- **`audioInterruption` is iOS only, by design.** The module never requests audio focus, so it never pauses or ducks your app or other apps. Android reports interruptions only through audio focus, so there is nothing to observe without it.

## Both platforms

- The module does not say **which app** is playing or calling, and does not distinguish GSM from VoIP calls.
