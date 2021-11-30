/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.os.Build
import com.ivianuu.injekt.Provide

data class DeviceInfo(val model: String, val manufacturer: String) {
  companion object {
    @Provide val androidDeviceInfo =
      DeviceInfo(model = Build.MODEL, manufacturer = Build.MANUFACTURER)
  }
}
