/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.*
import kotlinx.coroutines.flow.*

fun <T> Flow<T>.catchResult(): Flow<Result<T, Throwable>> = flow {
  try {
    this@catchResult.collect { emit(it.ok()) }
  } catch (e: Throwable) {
    emit(e.nonFatalOrThrow().err())
  }
}

fun <T> Flow<Result<T, Throwable>>.unwrapResult(): Flow<T> = flow {
  this@unwrapResult.collect {
    when (it) {
      is Ok -> emit(it.value)
      is Err -> throw it.error
    }
  }
}
