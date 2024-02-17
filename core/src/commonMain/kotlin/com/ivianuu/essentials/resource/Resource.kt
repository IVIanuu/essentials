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
import arrow.core.Either
import com.ivianuu.essentials.catch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Stable sealed interface Resource<out T> {
  data object Loading : Resource<Nothing>
  data class Success<T>(val value: T) : Resource<T>
  data class Error(val error: Throwable) : Resource<Nothing>
}

fun <T> Resource<T>.get(): T = if (this is Resource.Success) value else error("Called get() on a $this")

inline fun <T> Resource<T>.getOrElse(defaultValue: () -> T) =
  if (this is Resource.Success) value else defaultValue()

fun <T> Resource<T>.getOrNull() = getOrElse { null }

val Resource<*>.shouldLoad: Boolean get() = this is Resource.Error

val Resource<*>.isComplete: Boolean get() = this is Resource.Success || this is Resource.Error

inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> = when (this) {
  is Resource.Success -> Resource.Success(transform(value))
  else -> this as Resource<R>
}

inline fun <T, R> Resource<T>.flatMap(transform: (T) -> Resource<R>): Resource<R> = when (this) {
  is Resource.Success -> transform(value)
  else -> this as Resource<R>
}

fun <T> Flow<T>.flowAsResource(): Flow<Resource<T>> = resourceFlow {
  emitAll(this@flowAsResource)
}

fun <T> Flow<Resource<T>>.unwrapResource(): Flow<T> = flow {
  collect {
    when (it) {
      is Resource.Success -> emit(it.value)
      is Resource.Error -> throw it.error
      else -> {}
    }
  }
}

fun <T> resourceFlow(@BuilderInference block: suspend FlowCollector<T>.() -> Unit): Flow<Resource<T>> =
  flow<Resource<T>> {
    emit(Resource.Loading)
    catch {
      block(FlowCollector<T> { value -> this@flow.emit(Resource.Success(value)) })
    }.onLeft { emit(Resource.Error(it)) }
  }

fun <V> Either<Throwable, V>.toResource(): Resource<V> = fold(
  ifRight = { Resource.Success(it) },
  ifLeft = { Resource.Error(it) }
)

@Composable fun <T> Flow<T>.collectAsResourceState(
  vararg keys: Any?,
  context: CoroutineContext = EmptyCoroutineContext
): State<Resource<T>> {
  val state = remember(*keys) { mutableStateOf<Resource<T>>(Resource.Loading) }

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
