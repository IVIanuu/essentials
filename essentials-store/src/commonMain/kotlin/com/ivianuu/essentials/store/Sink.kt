package com.ivianuu.essentials.store

import com.ivianuu.injekt.Given
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

fun interface Sink<in T> {
    fun send(value: T)
}

@Given
fun <T> MutableSharedFlow<T>.asSink() = Sink<T> { tryEmit(it) }

fun <T> FlowCollector<T>.asSink(scope: CoroutineScope) = Sink<T> {
    scope.launch { emit(it) }
}

interface HasResult<R> {
    val result: CompletableDeferred<R>
}

suspend fun <T : HasResult<R>, R> Sink<T>.sendAndAwait(action: T): R {
    send(action)
    return action.result.await()
}
