/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

fun interface GlobalActionExecutor : suspend (Int) -> Boolean

@Provide fun globalActionExecutor(ref: Flow<EsAccessibilityService?>) =
  GlobalActionExecutor { action -> ref.first()?.performGlobalAction(action) ?: false }
