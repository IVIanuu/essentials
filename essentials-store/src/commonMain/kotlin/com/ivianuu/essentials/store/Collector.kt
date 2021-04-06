package com.ivianuu.essentials.store

import com.ivianuu.injekt.Given
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

fun interface Collector<T> {
    fun emit(value: T)
}

@Given
fun <T> MutableSharedFlow<T>.asCollector() = Collector<T> { tryEmit(it) }

fun <T> FlowCollector<T>.asCollector(scope: CoroutineScope) = Collector<T> {
    scope.launch { emit(it) }
}

interface HasResult<R> {
    val result: CompletableDeferred<R>
}

suspend fun <T : HasResult<R>, R> Collector<in T>.emitAndAwait(action: T): R {
    emit(action)
    return action.result.await()
}
