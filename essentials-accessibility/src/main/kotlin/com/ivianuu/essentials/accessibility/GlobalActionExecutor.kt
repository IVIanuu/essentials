/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import android.accessibilityservice.AccessibilityService
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

fun interface GlobalActionExecutor {
  suspend operator fun invoke(action: Int): Boolean
}

@Provide fun performGlobalAction(service: Flow<AccessibilityService?>) =
  GlobalActionExecutor { action ->
    service.first()?.performGlobalAction(action) ?: false
  }
