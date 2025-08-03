/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.xposed

import androidx.compose.ui.util.*
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage.*
import essentials.*
import injekt.*

abstract class EsXposedInit(
  private val buildXposedScope: (@Provide EsXposedInit).() -> Scope<XposedScope>
) : IXposedHookLoadPackage {
  @Provide lateinit var lpparam: LoadPackageParam
  @Provide val xposedAppScopeModule get() = XposedAppScopeModule

  override fun handleLoadPackage(lpparam: LoadPackageParam) {
    this.lpparam = lpparam
    xposedScope = buildXposedScope()
    xposedScope.service<XposedHooksComponent>().run {
      hooks.fastForEach { it.hook(config) }
    }
  }

  companion object {
    private lateinit var xposedScope: Scope<XposedScope>
  }
}

@JvmInline value class ModulePackageName(val value: String)
