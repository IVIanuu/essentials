@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials.resource

import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.optics.*
import kotlinx.coroutines.flow.*
import kotlin.jvm.*

sealed class Resource<out T>

object Idle : Resource<Nothing>() {
  override fun toString(): String = "Idle"
}

object Loading : Resource<Nothing>() {
  override fun toString(): String = "Loading"
}

data class Success<T>(val value: T) : Resource<T>()

data class Error(val error: Throwable) : Resource<Nothing>()

fun <T> Resource<T>.get(): T? = (this as? Success)?.value

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

fun <T> Flow<Result<T, Throwable>>.flowResultAsResource(): Flow<Resource<T>> = flow {
  emit(Loading)
  this@flowResultAsResource
    .map { it.toResource() }
    .let { emitAll(it) }
}

fun <T> resourceFlow(@BuilderInference block: suspend FlowCollector<T>.() -> Unit): Flow<Resource<T>> =
  flow<Resource<T>> {
    emit(Loading)
    catch {
      block(object : FlowCollector<T> {
        override suspend fun emit(value: T) {
          this@flow.emit(Success(value))
        }
      })
    }.onFailure { emit(Error(it)) }
  }

fun <V> Result<V, Throwable>.toResource(): Resource<V> = fold(
  success = { Success(it) },
  failure = { Error(it) }
)
