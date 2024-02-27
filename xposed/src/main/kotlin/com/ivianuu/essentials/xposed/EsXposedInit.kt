/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.*

abstract class EsXposedInit : IXposedHookLoadPackage {
  override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    xposedScope = buildXposedScope(lpparam, XposedAppScopeModule)
    xposedScope.service<XposedHooksComponent>().run {
      hooks.forEach { it(config) }
    }
  }

  protected abstract fun buildXposedScope(
    @Provide params: XC_LoadPackage.LoadPackageParam,
    @Provide module: XposedAppScopeModule
  ): Scope<XposedScope>

  companion object {
    private lateinit var xposedScope: Scope<XposedScope>
  }
}

@JvmInline value class ModulePackageName(val value: String)
