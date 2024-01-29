/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

fun CoroutineScope.childCoroutineScope(
  context: CoroutineContext = EmptyCoroutineContext,
): CoroutineScope = CoroutineScope(coroutineContext.childCoroutineContext(context))

suspend fun childCoroutineScope(context: CoroutineContext = EmptyCoroutineContext): CoroutineScope =
  CoroutineScope(coroutineContext.childCoroutineContext(context))

fun CoroutineContext.childCoroutineContext(context: CoroutineContext = EmptyCoroutineContext) =
  plus(context).let { it.plus(it.job.childJob()) }

fun Job.childJob() = Job(parent = this)
