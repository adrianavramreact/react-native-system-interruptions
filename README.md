# react-native-system-interruptions

Observe-only system interruptions for React Native (calls, audio interruptions, audio route, other audio) on iOS and Android

## Installation


```sh
npm install react-native-system-interruptions
```


## Usage


```js
import SystemInterruptions, {
  addListener,
} from 'react-native-system-interruptions';

// 1. Add the listener first.
const subscription = addListener('audioRoute', ({ routes }) => {
  console.log('audioRoute changed', routes);
});

// 2. Then read the initial state.
const routes = await SystemInterruptions.getAudioRouteAsync();

// Later
subscription.remove();
```

> **Order matters.** Events are emitted only when the state changes, never for the initial state. To miss nothing, add the listener first and read the initial state after it (for example `isInCallAsync()` or `getAudioRouteAsync()`). Reading it before `addListener` leaves a gap where a change is lost.

See [`LIMITATIONS.md`](LIMITATIONS.md) for platform limits.


## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
