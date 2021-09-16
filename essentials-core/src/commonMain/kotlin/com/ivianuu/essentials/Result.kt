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

package com.ivianuu.essentials

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import kotlin.coroutines.cancellation.CancellationException

inline fun <V> V.ok() = Ok(this)

inline fun <E> E.err() = Err(this)

fun <V> Result<V, *>.getOrNull(): V? = getOrElse { null }

inline fun <V> catch(@BuilderInference block: () -> V): Result<V, Throwable> = try {
  Ok(block())
} catch (e: CancellationException) {
  throw e
} catch (e: ControlException) {
  throw e
} catch (e: Throwable) {
  Err(e)
}

inline fun <V, E> result(@BuilderInference block: ResultBinding<E>.() -> V): Result<V, E> {
  @Suppress("UNCHECKED_CAST")
  return try {
    Ok(block(ResultBindingImpl as ResultBinding<E>))
  } catch (e: ResultBindingImpl.ShortCircuitException) {
    e.error as Err<E>
  }
}

interface ResultBinding<in A> {
  fun <T> Result<T, A>.bind(): T
}

@PublishedApi internal object ResultBindingImpl : ResultBinding<Nothing> {
  override fun <T> Result<T, Nothing>.bind(): T = when (this) {
    is Ok -> value
    is Err -> throw ShortCircuitException(this)
  }

  class ShortCircuitException(val error: Any?) : ControlException()
}
