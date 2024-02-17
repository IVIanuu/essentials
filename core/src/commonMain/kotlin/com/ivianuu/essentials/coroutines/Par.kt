package com.ivianuu.essentials.coroutines

import arrow.fx.coroutines.parMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun <T> par(
  vararg blocks: suspend () -> T,
  context: CoroutineContext = EmptyCoroutineContext
): List<T> = blocks.toList().parMap(context) { it() }
