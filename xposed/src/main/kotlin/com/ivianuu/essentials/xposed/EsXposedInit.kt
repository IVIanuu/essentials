/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.*

abstract class EsXposedInit : IXposedHookLoadPackage {
  override fun handleLoadPackage(@Provide lpparam: XC_LoadPackage.LoadPackageParam) {
    xposedScope = buildXposedScope()
    xposedScope.service<XposedHooksComponent>().run {
      hooks.forEach { it(config) }
    }
  }

  protected abstract fun buildXposedScope(@Inject params: XC_LoadPackage.LoadPackageParam): Scope<XposedScope>

  companion object {
    private lateinit var xposedScope: Scope<XposedScope>
  }
}

@JvmInline value class ModulePackageName(val value: String)
