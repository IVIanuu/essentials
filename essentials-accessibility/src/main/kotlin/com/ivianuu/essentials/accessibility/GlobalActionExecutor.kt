/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import androidx.compose.runtime.State
import com.ivianuu.injekt.Provide

fun interface GlobalActionExecutor : suspend (Int) -> Boolean

@Provide fun globalActionExecutor(ref: State<EsAccessibilityService?>) =
  GlobalActionExecutor { action -> ref.value?.performGlobalAction(action) ?: false }
