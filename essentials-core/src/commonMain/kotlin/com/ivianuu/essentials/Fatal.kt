package com.ivianuu.essentials

expect fun Throwable.isFatal(): Boolean

inline fun Throwable.nonFatalOrThrow(): Throwable = if (!isFatal()) this else throw this
