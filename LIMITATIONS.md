# Limitations

Known platform limits of `react-native-system-interruptions`. See `docs/DESIGN.md` for how each event is detected.

## iOS

- **`otherAudio` is foreground only.** In the background the app is suspended, and the `audio` background mode is only allowed for real playback ([App Review 2.5.4](https://developer.apple.com/app-store/review/guidelines/#2.5.4)). The module never activates the audio session, so there is no notification; the value is read with [`isOtherAudioPlaying`](https://developer.apple.com/documentation/avfaudio/avaudiosession/isotheraudioplaying) when the app becomes active.

## Android

- **`audioInterruption` is not emitted.** Android reports interruptions through audio focus, and the module never requests audio focus.
- **API 26–30: no live `call` updates yet.** The mode listener ([`addOnModeChangedListener`](https://developer.android.com/reference/android/media/AudioManager#addOnModeChangedListener(java.util.concurrent.Executor,%20android.media.AudioManager.OnModeChangedListener))) needs API 31. On older versions only the current mode can be read. The final approach is an open question in `docs/DESIGN.md`.

## Both platforms

- The module does not say **which app** is playing or calling, and does not distinguish GSM from VoIP calls.
