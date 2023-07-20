/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.scopeOfOrNull
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first

fun interface GlobalActionExecutor {
  suspend operator fun invoke(action: Int): Boolean
}

@Provide fun globalActionExecutor(appScope: Scope<AppScope>) =
  GlobalActionExecutor { action ->
    appScope.scopeOfOrNull<AccessibilityScope>().first()
      ?.accessibilityService?.performGlobalAction(action) == true
  }
