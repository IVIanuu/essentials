/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

expect fun Throwable.isFatal(): Boolean

inline fun Throwable.nonFatalOrThrow(): Throwable = if (!isFatal()) this else throw this
