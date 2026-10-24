import {
  TurboModuleRegistry,
  type CodegenTypes,
  type TurboModule,
} from 'react-native';

export type AudioRoute = 'bluetooth' | 'wired' | 'speaker';

export type CallEvent = 'ringing' | 'connected' | 'ended';
export type AudioInterruptionEvent = {
  state: 'began' | 'ended';
  shouldResume?: boolean;
};
export type AudioRouteEvent = { route: AudioRoute };
export type OtherAudioEvent = 'started' | 'stopped';

export interface Spec extends TurboModule {
  startObservingAsync(event: string): Promise<void>;
  stopObservingAsync(event: string): Promise<void>;

  isAvailableAsync(event: string): Promise<boolean>;
  isInCallAsync(): Promise<boolean>;
  getAudioRouteAsync(): Promise<AudioRoute>;
  isOtherAudioPlayingAsync(): Promise<boolean>;

  readonly onCall: CodegenTypes.EventEmitter<CallEvent>;
  readonly onAudioInterruption: CodegenTypes.EventEmitter<AudioInterruptionEvent>;
  readonly onAudioRoute: CodegenTypes.EventEmitter<AudioRouteEvent>;
  readonly onOtherAudio: CodegenTypes.EventEmitter<OtherAudioEvent>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('SystemInterruptions');
