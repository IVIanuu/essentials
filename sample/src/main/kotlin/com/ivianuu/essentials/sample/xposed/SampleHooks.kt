/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import android.bluetooth.BluetoothGattCharacteristic
import android.util.Base64
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.xposed.Hooks
import com.ivianuu.essentials.xposed.arg
import com.ivianuu.essentials.xposed.getField
import com.ivianuu.essentials.xposed.hookAllMethods
import com.ivianuu.injekt.Provide
import java.nio.charset.Charset

@Provide fun sampleHooks(L: Logger) = Hooks {
  if (packageName.value != "com.soundboks.SOUNDBOKS") return@Hooks

  hookAllMethods(
    classLoader.loadClass("com.polidea.multiplatformbleadapter.BleModule").kotlin,
    "writeCharacteristicWithValue"
  ) {
    after {
      val characteristic = arg<Any>(0).getField<BluetoothGattCharacteristic>("gattCharacteristic")
      val value = String(Base64.decode(arg<String>(1), Base64.NO_WRAP))
      log { "write characteristic $characteristic ${characteristic.uuid} $value" }
    }
  }
}
