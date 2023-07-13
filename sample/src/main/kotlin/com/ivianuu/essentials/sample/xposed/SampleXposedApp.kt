/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.contextWith
import com.ivianuu.essentials.xposed.EsXposedApp
import com.ivianuu.essentials.xposed.ModulePackageName
import com.ivianuu.essentials.xposed.XposedScope
import de.robv.android.xposed.callbacks.XC_LoadPackage

class SampleXposedApp : EsXposedApp() {
  context(XC_LoadPackage.LoadPackageParam) override fun buildXposedScope(): Scope<XposedScope> =
    contextWith(ModulePackageName("com.ivianuu.essentials.sample"))
}
