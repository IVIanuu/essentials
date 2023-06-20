/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials.resource

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.fold
import com.ivianuu.essentials.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Stable sealed interface Resource<out T>

object Idle : Resource<Nothing> {
  override fun toString(): String = "Idle"
}

object Loading : Resource<Nothing> {
  override fun toString(): String = "Loading"
}

data class Success<T>(val value: T) : Resource<T>

data class Error(val error: Throwable) : Resource<Nothing>

fun <T> Resource<T>.get(): T = if (this is Success) value else error("Called get() on a $this")

inline fun <T> Resource<T>.getOrElse(defaultValue: () -> T) =
  if (this is Success) value else defaultValue()

fun <T> Resource<T>.getOrNull() = getOrElse { null }

val Resource<*>.shouldLoad: Boolean get() = this is Idle || this is Error

val Resource<*>.isComplete: Boolean get() = this is Success || this is Error

inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> = when (this) {
  is Success -> Success(transform(value))
  else -> this as Resource<R>
}

inline fun <T, R> Resource<T>.flatMap(transform: (T) -> Resource<R>): Resource<R> = when (this) {
  is Success -> transform(value)
  else -> this as Resource<R>
}

fun <T> Flow<T>.flowAsResource(): Flow<Resource<T>> = resourceFlow {
  emitAll(this@flowAsResource)
}

fun <T> Flow<Resource<T>>.unwrapResource(): Flow<T> = flow {
  collect {
    when (it) {
      is Success -> emit(it.value)
      is Error -> throw it.error
      else -> {}
    }
  }
}

fun <T> resourceFlow(@BuilderInference block: suspend FlowCollector<T>.() -> Unit): Flow<Resource<T>> =
  flow<Resource<T>> {
    emit(Loading)
    catch {
      block(FlowCollector<T> { value -> this@flow.emit(Success(value)) })
    }.onFailure { emit(Error(it)) }
  }

fun <V> Result<V, Throwable>.toResource(): Resource<V> = fold(
  success = { Success(it) },
  failure = { Error(it) }
)

@Composable fun <T> Flow<T>.collectAsResourceState(
  vararg keys: Any?,
  context: CoroutineContext = EmptyCoroutineContext
): State<Resource<T>> {
  val state = remember(*keys) { mutableStateOf<Resource<T>>(Idle) }

  LaunchedEffect(this, context) {
    withContext(context) {
      flowAsResource()
        .collect {
          if (it.isComplete || !state.value.isComplete)
            state.value = it
        }
    }
  }

  return state
}

@Composable fun <T> produceResourceState(
  vararg keys: Any?,
  producer: suspend FlowCollector<T>.() -> Unit
): State<Resource<T>> = remember(*keys) { flow(producer) }
  .collectAsResourceState()
