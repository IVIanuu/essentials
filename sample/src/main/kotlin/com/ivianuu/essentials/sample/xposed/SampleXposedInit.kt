/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.*
import com.ivianuu.essentials.xposed.*
import com.ivianuu.injekt.*
import de.robv.android.xposed.callbacks.*

class SampleXposedInit : EsXposedInit() {
  override fun buildXposedScope(@Inject params: XC_LoadPackage.LoadPackageParam): Scope<XposedScope> {
    @Provide val modulePackageName = ModulePackageName("com.ivianuu.essentials.sample")
    return inject()
  }
}
