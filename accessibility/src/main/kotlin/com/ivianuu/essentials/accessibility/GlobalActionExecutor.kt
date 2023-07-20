/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import com.ivianuu.essentials.app.ScopeManager
import com.ivianuu.essentials.app.firstActiveScopeOrNull
import com.ivianuu.injekt.Provide

fun interface GlobalActionExecutor {
  suspend operator fun invoke(action: Int): Boolean
}

@Provide fun globalActionExecutor(scopeManager: ScopeManager) =
  GlobalActionExecutor { action ->
    scopeManager.firstActiveScopeOrNull<AccessibilityScope>()
      ?.accessibilityService?.performGlobalAction(action) == true
  }
