package com.ivianuu.essentials.coroutines

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

fun <T> Flow<Result<T, Throwable>>.unwrapResult() = flow {
  this@unwrapResult.collect {
    when (it) {
      is Ok -> emit(it.value)
      is Err -> throw it.error
    }
  }
}
