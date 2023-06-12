/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.essentials.Scope
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

private lateinit var xposedScope: Scope<XposedScope>

abstract class EsXposedApp : IXposedHookLoadPackage {
  override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    @Provide val context = XposedContextImpl(lpparam)
    xposedScope = buildXposedScope()
    xposedScope.service<XposedHooksComponent>().hooks().forEach { hooks ->
      with(hooks) {
        with(context) {
          invoke()
        }
      }
    }
  }

  protected abstract fun buildXposedScope(
    @Inject ctx: XposedContext
  ): Scope<XposedScope>
}
