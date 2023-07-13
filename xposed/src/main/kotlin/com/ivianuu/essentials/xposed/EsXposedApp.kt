/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.essentials.Scope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.provide
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

abstract class EsXposedApp : IXposedHookLoadPackage {
  override fun handleLoadPackage(@Provide lpparam: XC_LoadPackage.LoadPackageParam) {
    xposedScope = buildXposedScope()
    xposedScope.service<XposedHooksComponent>().run {
      hooks.forEach { hooks ->
        provide(config) { hooks() }
      }
    }
  }

  context(XC_LoadPackage.LoadPackageParam)
  protected abstract fun buildXposedScope(): Scope<XposedScope>

  companion object {
    private lateinit var xposedScope: Scope<XposedScope>
  }
}

@JvmInline value class ModulePackageName(val value: String)
