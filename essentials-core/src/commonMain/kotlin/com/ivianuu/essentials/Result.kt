package com.ivianuu.essentials

import com.github.michaelbull.result.*
import kotlin.coroutines.cancellation.*

inline fun <V> V.ok() = Ok(this)

inline fun <E> E.err() = Err(this)

fun <V> Result<V, *>.getOrNull(): V? = getOrElse { null }

inline fun <V> catch(@BuilderInference block: () -> V): Result<V, Throwable> = try {
  Ok(block())
} catch (e: CancellationException) {
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

@PublishedApi
internal object ResultBindingImpl : ResultBinding<Nothing> {
  override fun <T> Result<T, Nothing>.bind(): T = when (this) {
    is Ok -> value
    is Err -> throw ShortCircuitException(this)
  }

  class ShortCircuitException(val error: Any?) : ControlException()
}
