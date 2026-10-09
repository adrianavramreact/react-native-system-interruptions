# react-native-system-interruptions

TurboModule (iOS + Android) that **only observes** system interruptions. It never requests audio focus, never activates the iOS audio session, and never reads phone numbers or audio content.

- Events: `call` (ringing / connected / ended), `audioInterruption` (began / ended + `shouldResume`), `audioRoute` (bluetooth / wired / speaker), `otherAudio` (started / stopped).
- Use cases: media apps (pause/resume, debugging breadcrumbs), anti-fraud (active call during a transfer), online exams (call or headphones during the exam).

## Decisions already made
- TurboModule, not Nitro or Expo Modules (learn the fundamentals, zero dependencies). Nitro may come later as a comparison branch/article.
- Optional Expo config plugin (`app.plugin.js`), `READ_PHONE_STATE` opt-in.
- iOS `otherAudio` is foreground only (`isOtherAudioPlaying`); background is out of scope, document it in `LIMITATIONS.md`.
- The package collects no data. Privacy responsibility belongs to the app that sends events to a server (README "Privacy" section links Apple privacy manifest docs).

## Docs
- Repo docs: `docs/DESIGN.md`, `docs/adr/`, `LIMITATIONS.md`, `CHANGELOG.md`.
