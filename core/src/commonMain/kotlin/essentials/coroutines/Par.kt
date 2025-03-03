package essentials.coroutines

import arrow.fx.coroutines.*
import kotlin.coroutines.*

suspend fun <T> par(
  vararg blocks: suspend () -> T,
  context: CoroutineContext = EmptyCoroutineContext
): List<T> = blocks.toList().parMap(context) { it() }
