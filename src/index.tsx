import SystemInterruptions from './NativeSystemInterruptions';
import type { EventSubscription } from 'react-native';
import type {
  AudioRoute,
  CallEvent,
  AudioInterruptionEvent,
  AudioRouteEvent,
  OtherAudioEvent,
} from './NativeSystemInterruptions';

export type {
  AudioRoute,
  CallEvent,
  AudioInterruptionEvent,
  AudioRouteEvent,
  OtherAudioEvent,
};
type EventMap = {
  call: CallEvent;
  audioInterruption: AudioInterruptionEvent;
  audioRoute: AudioRouteEvent;
  otherAudio: OtherAudioEvent;
};
// Number of active listeners per event. Native observing runs only while it is > 0.
const counts: Partial<Record<keyof EventMap, number>> = {};

export function addListener<K extends keyof EventMap>(
  eventName: K,
  listener: (event: EventMap[K]) => void
): EventSubscription {
  const subscription = subscribe(eventName, listener);
  const count = counts[eventName] ?? 0;
  if (count === 0) {
    SystemInterruptions.startObservingAsync(eventName).catch((e) =>
      console.warn(`startObservingAsync('${eventName}') failed`, e)
    );
  }
  counts[eventName] = count + 1;

  let removed = false;
  return {
    remove() {
      if (removed) return;
      removed = true;
      subscription.remove();
      const left = counts[eventName]! - 1;
      counts[eventName] = left;
      if (left === 0) {
        SystemInterruptions.stopObservingAsync(eventName).catch((e) =>
          console.warn(`stopObservingAsync('${eventName}') failed`, e)
        );
      }
    },
  };
}

function subscribe<K extends keyof EventMap>(
  eventName: K,
  listener: (event: EventMap[K]) => void
): EventSubscription {
  switch (eventName) {
    case 'call':
      return SystemInterruptions.onCall(listener as (event: CallEvent) => void);
    case 'audioInterruption':
      return SystemInterruptions.onAudioInterruption(
        listener as (event: AudioInterruptionEvent) => void
      );
    case 'audioRoute':
      return SystemInterruptions.onAudioRoute(
        listener as (event: AudioRouteEvent) => void
      );
    case 'otherAudio':
      return SystemInterruptions.onOtherAudio(
        listener as (event: OtherAudioEvent) => void
      );
  }
}
export default {
  isAvailableAsync: (event: keyof EventMap) =>
    SystemInterruptions.isAvailableAsync(event),
  isInCallAsync: SystemInterruptions.isInCallAsync,
  getAudioRouteAsync: SystemInterruptions.getAudioRouteAsync,
  isOtherAudioPlayingAsync: SystemInterruptions.isOtherAudioPlayingAsync,
  addListener,
};
