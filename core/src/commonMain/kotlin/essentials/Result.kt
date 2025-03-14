package essentials

import arrow.core.*
import com.github.michaelbull.result.*

inline fun <T> catch(block: () -> T): Result<T, Throwable> = try {
  Ok(block())
} catch (t: Throwable) {
  Err(t.nonFatalOrThrow())
}

fun <V, E : Throwable> Result<V, E>.printErrors() = onFailure {
  it.printStackTrace()
}

fun <V> V.ok() = Ok(this)

fun <E> E.err() = Err(this)

fun <V> Result<V, *>.getOrNull() = getOrElse { null }
