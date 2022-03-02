/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.*

fun CoroutineScope.childCoroutineScope(
  context: CoroutineContext = EmptyCoroutineContext,
): CoroutineScope = CoroutineScope(coroutineContext.childCoroutineContext(context))

suspend fun childCoroutineScope(context: CoroutineContext = EmptyCoroutineContext): CoroutineScope =
  CoroutineScope(coroutineContext.childCoroutineContext(context))

fun CoroutineContext.childCoroutineContext(context: CoroutineContext = EmptyCoroutineContext) =
  plus(plus(context).job.childJob())

fun Job.childJob() = Job(parent = this)
