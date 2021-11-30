/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.os.Build
import com.ivianuu.injekt.Provide

data class SystemBuildInfo(val sdk: Int) {
  companion object {
    @Provide val androidSystemBuildInfo = SystemBuildInfo(Build.VERSION.SDK_INT)
  }
}
