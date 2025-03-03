package essentials

import arrow.core.*

inline fun <T> catch(block: () -> T) = Either.catch(f = block)

fun <A> Either<Throwable, A>.recover(block: () -> A) = when (this) {
  is Either.Left -> catch(block)
  is Either.Right -> this
}

fun <A : Throwable, B> Either<A, B>.printErrors() = onLeft {
  it.printStackTrace()
}
