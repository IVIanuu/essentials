/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.*

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

  protected abstract fun buildXposedElements(
    @Inject context: XposedContext,
    scope: Scope<XposedScope>
  ): Elements<XposedScope>
}
