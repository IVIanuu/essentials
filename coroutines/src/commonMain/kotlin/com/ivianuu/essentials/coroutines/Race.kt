/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Deprecated(message = "A race needs racers", level = DeprecationLevel.ERROR)
suspend fun <T> race(context: CoroutineContext = EmptyCoroutineContext) {
}

suspend fun <T> race(
  vararg racers: suspend CoroutineScope.() -> T,
  context: CoroutineContext = EmptyCoroutineContext
): T = coroutineScope {
  val allRacers = racers.map { async(context = context, block = it) }
  select {
    allRacers.forEach { deferredRacer ->
      deferredRacer.onAwait { result ->
        allRacers.forEach { it.cancel() }
        result
      }
    }
  }
}