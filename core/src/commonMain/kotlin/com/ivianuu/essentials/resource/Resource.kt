/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials.resource

import androidx.compose.runtime.*
import arrow.core.*
import com.ivianuu.essentials.*
import kotlinx.coroutines.flow.*

@Stable sealed interface Resource<out T> {
  data object Idle : Resource<Nothing>
  data object Loading : Resource<Nothing>
  data class Success<T>(val value: T) : Resource<T>
  data class Error(val error: Throwable) : Resource<Nothing>

  companion object {
    fun <T> Idle(): Resource<T> = Idle
    fun <T> Loading(): Resource<T> = Loading
  }
}

fun <T> T.success() = Resource.Success(this)

fun Throwable.error() = Resource.Error(this)

fun <T> Resource<T>.get(): T = if (this is Resource.Success) value else error("Called get() on a $this")

inline fun <T> Resource<T>.getOrElse(defaultValue: () -> T) =
  if (this is Resource.Success) value else defaultValue()

fun <T> Resource<T>.getOrNull() = getOrElse { null }

val Resource<*>.shouldLoad: Boolean get() = this is Resource.Idle || this is Resource.Error

val Resource<*>.isComplete: Boolean get() = this is Resource.Success || this is Resource.Error

inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> = when (this) {
  is Resource.Success -> transform(value).success()
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
      block(FlowCollector<T> { value -> this@flow.emit(value.success()) })
    }.onLeft { emit(it.error()) }
  }

fun <V> Either<Throwable, V>.toResource(): Resource<V> = fold(
  ifRight = { it.success() },
  ifLeft = { it.error() }
)

fun <T> Resource<T>.printErrors() = apply {
  if (this is Resource.Error)
    error.printStackTrace()
}

inline fun <T> catchResource(block: () -> T) = try {
  block().success()
} catch (e: Throwable) {
  e.nonFatalOrThrow()
  e.error()
}
