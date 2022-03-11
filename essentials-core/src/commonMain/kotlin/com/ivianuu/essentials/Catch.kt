/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import com.github.michaelbull.result.*

inline fun <T> catch(block: () -> T): Result<T, Throwable> = try {
  Ok(block())
} catch (e: Throwable) {
  Err(e.nonFatalOrThrow())
}

inline fun <R, T> R.catch(block: R.() -> T): Result<T, Throwable> = try {
  Ok(block())
} catch (e: Throwable) {
  Err(e.nonFatalOrThrow())
}

inline fun <T, reified E> catchT(block: () -> T): Result<T, E> = try {
  Ok(block())
} catch (e: Throwable) {
  if (e is E)
    Err(e)
  else
    throw e
}

inline fun <R, T, reified E> R.catchT(block: R.() -> T): Result<T, E> = try {
  Ok(block())
} catch (e: Throwable) {
  if (e is E)
    Err(e)
  else
    throw e
}
