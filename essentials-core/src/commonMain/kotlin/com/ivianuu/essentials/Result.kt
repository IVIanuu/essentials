package com.ivianuu.essentials

sealed class Result<out V, out E>

data class Ok<V>(val value: V) : Result<V, Nothing>()
data class Err<E>(val error: E) : Result<Nothing, E>()

inline val Result<*, *>.isOk: Boolean get() = this is Ok
inline val Result<*, *>.isErr: Boolean get() = this is Err

operator fun <V> Result<V, *>.component1(): V? = (this as? Ok)?.value
operator fun <E> Result<*, E>.component2(): E? = (this as? Err)?.error

inline fun <V, E, W> Result<V, E>.map(transform: (V) -> W): Result<W, E> = when (this) {
  is Ok -> Ok(transform(value))
  is Err -> this
}

inline fun <V, E, F> Result<V, E>.mapError(transform: (E) -> F): Result<V, F> = when (this) {
  is Ok -> this
  is Err -> Err(transform(error))
}

inline fun <V, E, U> Result<V, E>.fold(success: (V) -> U, failure: (E) -> U): U = when (this) {
  is Ok -> success(value)
  is Err -> failure(error)
}

inline fun <V, E, U> Result<V, E>.flatMap(transform: (V) -> Result<U, E>): Result<U, E> =
  when (this) {
    is Ok -> transform(value)
    is Err -> this
  }

fun <V> Result<V, *>.get(): V = when (this) {
  is Ok -> value
  is Err -> throw IllegalStateException("Called get() on a Err type $error")
}

inline fun <V, E> Result<V, E>.getOrElse(defaultValue: (E) -> V): V = when (this) {
  is Ok -> value
  is Err -> defaultValue(error)
}

fun <V> Result<V, *>.getOrNull(): V? = getOrElse { null }

fun <E> Result<*, E>.getError(): E = when (this) {
  is Ok -> throw IllegalStateException("Called getError() on a Ok type $value")
  is Err -> error
}

inline fun <V, E> Result<V, E>.getErrorOrElse(defaultValue: (V) -> E): E = when (this) {
  is Ok -> defaultValue(value)
  is Err -> error
}

fun <E> Result<*, E>.getErrorOrNull(): E? = (this as? Err)?.error

inline fun <V, E> Result<V, E>.recover(transform: (E) -> V): Ok<V> = when (this) {
  is Ok -> this
  is Err -> Ok(transform(error))
}

inline fun <V, E> Result<V, E>.onSuccess(action: (V) -> Unit): Result<V, E> {
  if (this is Ok) action(value)
  return this
}

inline fun <V, E> Result<V, E>.onFailure(action: (E) -> Unit): Result<V, E> {
  if (this is Err) action(error)
  return this
}

inline fun <V, E> Result<V, E>.onEither(success: (V) -> Unit, failure: (E) -> Unit): Result<V, E> {
  when (this) {
    is Ok -> success(value)
    is Err -> failure(error)
  }

  return this
}

inline fun <V> V.ok() = Ok(this)

inline fun <E> E.err() = Err(this)

inline fun <V> catch(@BuilderInference block: () -> V): Result<V, Throwable> = try {
  Ok(block())
} catch (e: Throwable) {
  Err(e.nonFatalOrThrow())
}

inline fun <V, reified T> catchT(@BuilderInference block: () -> V): Result<V, T> = try {
  Ok(block())
} catch (e: Throwable) {
  if (e is T) Err(e)
  else throw e
}

@Suppress("UNCHECKED_CAST")
inline fun <V, E> result(@BuilderInference block: ResultBinding<E>.() -> V): Result<V, E> = try {
  Ok(block(ResultBindingImpl as ResultBinding<E>))
} catch (e: ResultBindingImpl.ShortCircuitException) {
  e.error as Err<E>
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
