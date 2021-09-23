/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials.resource

import com.ivianuu.essentials.Result
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.fold
import com.ivianuu.essentials.onFailure
import kotlinx.coroutines.flow.*

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

fun <T> Flow<Resource<T>>.unwrapResource(): Flow<T> = flow {
  this@unwrapResource.collect { value ->
    when (value) {
      is Success -> emit(value.value)
      is Error -> throw value.error
    }
  }
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
