/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.xposed.EsXposedApp
import com.ivianuu.essentials.xposed.ModulePackageName
import com.ivianuu.essentials.xposed.XposedScope
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Providers
import com.ivianuu.injekt.inject
import de.robv.android.xposed.callbacks.XC_LoadPackage

class SampleXposedApp : EsXposedApp() {
  override fun buildXposedScope(@Inject llparam: XC_LoadPackage.LoadPackageParam): Scope<XposedScope> {
    @Provide val modulePackageName = ModulePackageName("com.ivianuu.essentials.sample")
    @Providers(".**") return inject()
  }
}
