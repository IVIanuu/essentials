/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.result.Result
import com.ivianuu.essentials.result.failure
import com.ivianuu.essentials.nonFatalOrThrow
import com.ivianuu.essentials.result.success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.catchResult(): Flow<Result<T, Throwable>> = flow {
  try {
    this@catchResult.collect { emit(it.success()) }
  } catch (e: Throwable) {
    emit(e.nonFatalOrThrow().failure())
  }
}

fun <T> Flow<Result<T, Throwable>>.unwrapResult(): Flow<T> = flow {
  this@unwrapResult.collect {
    when (it) {
      is Result.Success -> emit(it.value)
      is Result.Failure -> throw it.error
    }
  }
}
