package com.ivianuu.essentials.util

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.unwrap
import com.ivianuu.essentials.coroutines.flowOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

fun <V> catchingFlowOf(block: suspend () -> V): Flow<Result<V, Throwable>> = flowOf(block)
    .flowCatching()

fun <V> Flow<V>.flowCatching(): Flow<Result<V, Throwable>> {
    return map<V, Result<V, Throwable>> { Ok(it) }
        .catch { emit(Err(it)) }
}

fun <V, E> Flow<Result<V, E>>.unwrap(): Flow<V> {
    return map { it.unwrap() }
}

@JvmName("unwrapThrowable")
fun <V> Flow<Result<V, Throwable>>.unwrap(): Flow<V> {
    return transform {
        when (it) {
            is Ok -> emit(it.value)
            is Err -> throw it.error
        }
    }
}
