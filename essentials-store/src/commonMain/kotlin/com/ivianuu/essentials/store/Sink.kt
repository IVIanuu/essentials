package com.ivianuu.essentials.store

import com.ivianuu.injekt.Given
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
