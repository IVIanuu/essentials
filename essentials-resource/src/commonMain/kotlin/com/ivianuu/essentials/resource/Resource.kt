/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials.resource

import com.github.michaelbull.result.*
import kotlinx.coroutines.flow.*
import kotlin.onFailure
import kotlin.runCatching

sealed interface Resource<out T>

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

fun <T> resourceFlow(@BuilderInference block: suspend FlowCollector<T>.() -> Unit): Flow<Resource<T>> =
  flow<Resource<T>> {
    emit(Loading)
    runCatching {
      block(FlowCollector<T> { value -> this@flow.emit(Success(value)) })
    }.onFailure { emit(Error(it)) }
  }

fun <V> Result<V, Throwable>.toResource(): Resource<V> = fold(
  success = { Success(it) },
  failure = { Error(it) }
)
