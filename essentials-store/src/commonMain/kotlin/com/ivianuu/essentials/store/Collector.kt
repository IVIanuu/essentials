package com.ivianuu.essentials.store

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.FlowCollector

interface Collector<T> : FlowCollector<T> {
    fun tryEmit(action: T): Boolean
}

suspend fun <T : CompletableDeferred<S>, S> Collector<in T>.emitAndAwait(action: T): S {
    emit(action)
    return action.await()
}
