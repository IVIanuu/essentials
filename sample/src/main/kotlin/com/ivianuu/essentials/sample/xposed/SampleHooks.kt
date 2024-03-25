/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import android.bluetooth.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.xposed.*
import com.ivianuu.injekt.*

@Provide fun sampleHooks(logger: Logger) = Hooks { config ->
  logger.d { "hello ${config.packageName}" }
  if (config.packageName != "uk.co.minirig.app") return@Hooks

  logger.d { "minirig minirig minirig" }

  hookAllMethods(BluetoothGatt::class, "connect") {
    before {
      logger.d { "connect ${args.contentDeepToString()}" }
    }
  }

  hookAllMethods(BluetoothGatt::class, "writeCharacteristic") {
    before {
      logger.d { "minirig message ${args.contentDeepToString()}" }
    }
  }
}
