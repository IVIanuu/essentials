/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.xposed.*
import injekt.*

class SampleXposedInit : EsXposedInit({
  @Provide val modulePackageName = ModulePackageName("com.ivianuu.essentials.sample")
  inject()
})
