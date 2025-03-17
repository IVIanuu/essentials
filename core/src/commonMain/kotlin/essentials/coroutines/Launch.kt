package essentials.coroutines

import injekt.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

fun launch(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  scope: CoroutineScope = inject,
  block: suspend (@Provide CoroutineScope) -> Unit
) = scope.launch(context, start, block)

fun <T> async(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  scope: CoroutineScope = inject,
  block: suspend (@Provide CoroutineScope) -> T
) = scope.async<T>(context, start, block)
