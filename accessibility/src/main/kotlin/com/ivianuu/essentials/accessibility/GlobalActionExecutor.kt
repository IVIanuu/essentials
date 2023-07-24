/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import com.ivianuu.essentials.ScopeManager
import com.ivianuu.essentials.scopeOfOrNull
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first

fun interface GlobalActionExecutor {
  suspend operator fun invoke(action: Int): Boolean
}

@Provide fun globalActionExecutor(scopeManager: ScopeManager) =
  GlobalActionExecutor { action ->
    scopeManager.scopeOfOrNull<AccessibilityScope>().first()
      ?.accessibilityService?.performGlobalAction(action) == true
  }
