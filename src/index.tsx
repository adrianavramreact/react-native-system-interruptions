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
export function addListener<K extends keyof EventMap>(
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
  startObservingAsync: SystemInterruptions.startObservingAsync,
  stopObservingAsync: SystemInterruptions.stopObservingAsync,
  isInCallAsync: SystemInterruptions.isInCallAsync,
  getAudioRouteAsync: SystemInterruptions.getAudioRouteAsync,
  addListener,
};
