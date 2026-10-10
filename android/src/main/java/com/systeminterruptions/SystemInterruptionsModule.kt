package com.systeminterruptions

import com.facebook.react.bridge.ReactApplicationContext

class SystemInterruptionsModule(reactContext: ReactApplicationContext) :
  NativeSystemInterruptionsSpec(reactContext) {

  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  companion object {
    const val NAME = NativeSystemInterruptionsSpec.NAME
  }
}
