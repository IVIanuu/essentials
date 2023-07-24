/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.Service
import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Provide

@Provide data object XposedScope {
  @Provide fun appScope(xposedScope: Scope<XposedScope>): Scope<AppScope> = xposedScope.cast()
}

@Provide @Service<XposedScope> data class XposedHooksComponent(
  val config: XposedConfig,
  val hooks: List<Hooks>
)
