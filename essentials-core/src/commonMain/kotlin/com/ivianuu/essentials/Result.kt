/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import androidx.compose.runtime.Stable

@Stable sealed interface Result<out V, out E> {
  data class Success<V>(val value: V) : Result<V, Nothing>
  data class Failure<E>(val error: E) : Result<Nothing, E>
}

inline val Result<*, *>.isSuccess: Boolean get() = this is Result.Success
inline val Result<*, *>.isFailure: Boolean get() = this is Result.Failure

operator fun <V> Result<V, *>.component1(): V? = (this as? Result.Success)?.value
operator fun <E> Result<*, E>.component2(): E? = (this as? Result.Failure)?.error

inline fun <V, E, W> Result<V, E>.map(transform: (V) -> W): Result<W, E> = when (this) {
  is Result.Success -> transform(value).success()
  is Result.Failure -> this
}

inline fun <V, E, F> Result<V, E>.mapFailure(transform: (E) -> F): Result<V, F> = when (this) {
  is Result.Success -> this
  is Result.Failure -> transform(error).failure()
}

inline fun <V, E, U> Result<V, E>.fold(success: (V) -> U, failure: (E) -> U): U = when (this) {
  is Result.Success -> success(value)
  is Result.Failure -> failure(error)
}

inline fun <V, E, U> Result<V, E>.flatMap(transform: (V) -> Result<U, E>): Result<U, E> =
  when (this) {
    is Result.Success -> transform(value)
    is Result.Failure -> this
  }

fun <V> Result<V, *>.get(): V = when (this) {
  is Result.Success -> value
  is Result.Failure -> error("Called get() on a Failure type $error")
}

inline fun <V, E> Result<V, E>.getOrElse(defaultValue: (E) -> V): V = when (this) {
  is Result.Success -> value
  is Result.Failure -> defaultValue(error)
}

fun <V> Result<V, *>.getOrNull(): V? = getOrElse { null }

fun <V> Result<V, Throwable>.getOrThrow(): V = getOrElse { throw it }

fun <E> Result<*, E>.getFailure(): E = when (this) {
  is Result.Success -> error("Called getError() on a Ok type $value")
  is Result.Failure -> error
}

inline fun <V, E> Result<V, E>.getFailureOrElse(defaultValue: (V) -> E): E = when (this) {
  is Result.Success -> defaultValue(value)
  is Result.Failure -> error
}

fun <E> Result<*, E>.getFailureOrNull(): E? = (this as? Result.Failure)?.error

inline fun <V, E> Result<V, E>.recover(transform: (E) -> V): Result.Success<V> = when (this) {
  is Result.Success -> this
  is Result.Failure -> transform(error).success()
}

inline fun <V, E> Result<V, E>.onSuccess(action: (V) -> Unit): Result<V, E> {
  if (this is Result.Success) action(value)
  return this
}

inline fun <V, E> Result<V, E>.onFailure(action: (E) -> Unit): Result<V, E> {
  if (this is Result.Failure) action(error)
  return this
}

inline fun <V> V.success() = Result.Success(this)

inline fun <E> E.failure() = Result.Failure(this)

inline fun <V> catch(@BuilderInference block: ResultControl<Throwable>.() -> V): Result<V, Throwable> =
  try {
    block(ResultControlImpl.cast()).success()
  } catch (e: Throwable) {
    e.nonFatalOrThrow().failure()
  }

inline fun <V, reified T> catchT(@BuilderInference block: ResultControl<T>.() -> V): Result<V, T> =
  try {
    block(ResultControlImpl.cast()).success()
  } catch (e: Throwable) {
    if (e is T) e.failure()
    else throw e
  }

@Suppress("UNCHECKED_CAST")
inline fun <V, E> result(@BuilderInference block: ResultControl<E>.() -> V): Result<V, E> =
  try {
    block(ResultControlImpl.cast()).success()
  } catch (e: ResultControlImpl.ShortCircuitException) {
    e.error as Result.Failure<E>
  }

interface ResultControl<in A> {
  fun <T> bind(result: Result<T, A>): T
}

@PublishedApi internal object ResultControlImpl : ResultControl<Nothing> {
  override fun <T> bind(result: Result<T, Nothing>): T = when (result) {
    is Result.Success -> result.value
    is Result.Failure -> throw ShortCircuitException(this)
  }

  class ShortCircuitException(val error: Any?) : ControlException()
}
