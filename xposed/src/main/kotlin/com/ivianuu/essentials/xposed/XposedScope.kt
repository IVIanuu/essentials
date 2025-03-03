/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.essentials.*
import injekt.*

data object XposedScope

object XposedAppScopeModule {
  @Provide fun appScope(xposedScope: Scope<XposedScope>): Scope<AppScope> = xposedScope.cast()
}

@Provide @Service<XposedScope> data class XposedHooksComponent(
  val config: XposedConfig,
  val hooks: List<Hooks>
)
