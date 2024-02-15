package com.ivianuu.essentials

import arrow.core.Either

fun <A> Either<Throwable, A>.recover(block: () -> A) = when (this) {
  is Either.Left -> Either.catch(block)
  is Either.Right -> this
}

fun <A : Throwable, B> Either<A, B>.printErrors() = onLeft {
  it.printStackTrace()
}
