/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

fun interface GlobalActionExecutor {
  suspend fun performGlobalAction(action: Int): Boolean
}

@Provide fun performGlobalAction(ref: Flow<EsAccessibilityService?>) =
  GlobalActionExecutor { action -> ref.first()?.performGlobalAction(action) ?: false }
