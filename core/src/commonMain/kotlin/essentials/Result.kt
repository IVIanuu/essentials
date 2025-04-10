package essentials

import arrow.core.*
import com.github.michaelbull.result.*

inline fun <T> catch(block: () -> T): Result<T, Throwable> = try {
  block().ok()
} catch (t: Throwable) {
  t.nonFatalOrThrow().err()
}

inline fun <T, S> Result<T, Throwable>.catchMap(block: (T) -> S): Result<S, Throwable> =
  flatMap { catch { block(it) } }

fun <V, E : Throwable> Result<V, E>.printErrors() = onFailure {
  it.printStackTrace()
}

fun <V> V.ok() = Ok(this)

fun <E> E.err() = Err(this)

fun <V> Result<V, *>.getOrNull() = getOrElse { null }
