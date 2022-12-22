/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

private lateinit var appScope: Scope<XposedScope>

abstract class EsXposedApp : IXposedHookLoadPackage {
  override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    @Provide val context = XposedContextImpl(lpparam)
    @Provide val scope = Scope<XposedScope>()
      .also { appScope = it }
    @Provide val elements = buildXposedElements()
    elements<XposedHooksComponent>().hooks().forEach { hooks ->
      with(hooks) {
        with(context) {
          invoke()
        }
      }
    }
  }

  context(XposedContext, Scope<XposedScope>)
  protected abstract fun buildXposedElements(): Elements<XposedScope>
}
