package essentials.coroutines

import arrow.fx.coroutines.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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

fun <T> EventFlow(): MutableSharedFlow<T> = MutableSharedFlow(
  extraBufferCapacity = Int.MAX_VALUE
)

suspend inline fun <A> bracketCase(
  crossinline acquire: suspend () -> A,
  crossinline release: suspend (A, ExitCase) -> Unit
): Nothing = bracketCase(acquire = acquire, use = { awaitCancellation() }, release = release)

suspend inline fun onCancel(crossinline onCancel: suspend () -> Unit): Nothing =
  onCancel(fa = { awaitCancellation() }, onCancel = onCancel)

suspend fun <T> par(
  vararg blocks: suspend () -> T,
  context: CoroutineContext = EmptyCoroutineContext
): List<T> = blocks.toList().parMap(context) { it() }

