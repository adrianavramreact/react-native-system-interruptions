# Limitations

Known platform limits of `react-native-system-interruptions`. See `docs/DESIGN.md` for how each event is detected.

## iOS

- **`otherAudio` is foreground only.** In the background the app is suspended, and the `audio` background mode is only allowed for real playback ([App Review 2.5.4](https://developer.apple.com/app-store/review/guidelines/#2.5.4)). The module never activates the audio session, so there is no notification; the value is read with [`isOtherAudioPlaying`](https://developer.apple.com/documentation/avfaudio/avaudiosession/isotheraudioplaying) when the app becomes active.

## Android

- **`audioInterruption` is not emitted.** Android reports interruptions through audio focus, and the module never requests audio focus.
- **Below API 31: no `call` events.** The mode listener ([`addOnModeChangedListener`](https://developer.android.com/reference/android/media/AudioManager#addOnModeChangedListener(java.util.concurrent.Executor,%20android.media.AudioManager.OnModeChangedListener))) needs API 31. `isInCallAsync()` rejects with code `ERR_UNSUPPORTED`.
- **Below API 26: observing is not supported.** [`AudioPlaybackCallback`](https://developer.android.com/reference/android/media/AudioManager.AudioPlaybackCallback) and [`getActivePlaybackConfigurations`](https://developer.android.com/reference/android/media/AudioManager#getActivePlaybackConfigurations()) need API 26. `startObservingAsync()`, `stopObservingAsync()` and `isOtherAudioPlayingAsync()` reject with code `ERR_UNSUPPORTED`, so no events are sent.
- **`audioRoute` reports only `bluetooth`, `wired` and `speaker`.** Other types are not reported. They can be added on request, after more testing on real devices.
- **`call` covers ringing, cellular and VoIP calls.** Call screening (`MODE_CALL_SCREENING`) and calls moved to another device (`MODE_CALL_REDIRECT`, `MODE_COMMUNICATION_REDIRECT`) are planned for a future release.

## Both platforms

- The module does not say **which app** is playing or calling, and does not distinguish GSM from VoIP calls.
