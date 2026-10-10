import SystemInterruptions from './NativeSystemInterruptions';

export function multiply(a: number, b: number): number {
  return SystemInterruptions.multiply(a, b);
}
