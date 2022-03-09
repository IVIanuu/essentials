/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.xposed.*

@Provide fun sampleHooks(L: Logger) = Hooks {
  log { "hello from xposed $packageName" }
}
