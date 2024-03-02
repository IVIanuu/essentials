/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import android.bluetooth.*
import co.touchlab.kermit.*
import com.ivianuu.essentials.xposed.*
import com.ivianuu.injekt.*

@Provide fun sampleHooks(logger: Logger) = Hooks { config ->
  logger.d { "hello ${config.packageName}" }
  if (config.packageName != "com.apelabs.wapp") return@Hooks

  logger.d { "ape ape ape" }

  hookAllMethods(BluetoothGatt::class, "writeCharacteristic") {
    before {
      logger.d { "ape message ${args.contentDeepToString()}" }
    }
  }
}
