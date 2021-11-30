/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

inline fun launch(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  @Inject scope: CoroutineScope,
  noinline block: suspend CoroutineScope.() -> Unit
) = scope.launch(context, start, block)

inline fun <T> async(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  @Inject scope: CoroutineScope,
  noinline block: suspend CoroutineScope.() -> T
) = scope.async(context, start, block)