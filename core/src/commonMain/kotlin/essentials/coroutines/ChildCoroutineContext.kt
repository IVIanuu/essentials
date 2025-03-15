/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.*

fun CoroutineScope.childCoroutineScope(
  context: CoroutineContext = EmptyCoroutineContext,
): CoroutineScope = CoroutineScope(coroutineContext.childCoroutineContext(context))

fun CoroutineContext.childCoroutineContext(context: CoroutineContext = EmptyCoroutineContext) =
  plus(context).let { it.plus(it.job.childJob()) }

fun Job.childJob() = Job(parent = this)
