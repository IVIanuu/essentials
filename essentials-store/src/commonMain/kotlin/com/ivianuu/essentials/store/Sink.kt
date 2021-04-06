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

interface ResultAction<R> {
    val hasResult: Boolean
    suspend fun await(): R
    fun set(value: R)
}

fun <R> ResultAction(): ResultAction<R> = ResultActionImpl()

private class ResultActionImpl<R> : ResultAction<R> {
    private val completableDeferred = CompletableDeferred<R>()
    override val hasResult: Boolean
        get() = completableDeferred.isCompleted
    override suspend fun await(): R = completableDeferred.await()
    override fun set(value: R) {
        completableDeferred.complete(value)
    }
}

suspend fun <T : ResultAction<R>, R> Sink<T>.sendAndAwait(action: T): R {
    send(action)
    return action.await()
}
