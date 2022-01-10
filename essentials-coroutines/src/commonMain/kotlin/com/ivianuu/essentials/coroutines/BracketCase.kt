/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.*
import kotlinx.coroutines.*

sealed interface ExitCase {
  object Completed : ExitCase {
    override fun toString(): String = "ExitCase.Completed"
  }

  data class Cancelled(val exception: CancellationException) : ExitCase
  data class Failure(val failure: Throwable) : ExitCase
}

suspend inline fun onCancel(
  crossinline onCancel: suspend () -> Unit
): Nothing = onCancel(block = { awaitCancellation() }, onCancel)

suspend inline fun <R> onCancel(
  block: suspend () -> R,
  crossinline onCancel: suspend () -> Unit
): R = guarantee(block) { case ->
  when (case) {
    is ExitCase.Cancelled -> onCancel()
    else -> Unit
  }
}

suspend inline fun <R> guarantee(
  block: suspend () -> R,
  crossinline finalizer: suspend (ExitCase) -> Unit
): R {
  val result = try {
    block()
  } catch (e: CancellationException) {
    runReleaseAndRethrow(e) { finalizer(ExitCase.Cancelled(e)) }
  } catch (t: Throwable) {
    runReleaseAndRethrow(t.nonFatalOrThrow()) { finalizer(ExitCase.Failure(t)) }
  }

  withContext(NonCancellable) { finalizer(ExitCase.Completed) }

  return result
}

suspend inline fun <T> bracket(
  crossinline acquire: suspend () -> T,
  crossinline release: suspend (T, ExitCase) -> Unit
): Nothing = bracket(acquire, { awaitCancellation() }, release)

suspend inline fun <T, R> bracket(
  crossinline acquire: suspend () -> T,
  use: suspend (T) -> R,
  crossinline release: suspend (T, ExitCase) -> Unit
): R {
  val acquired = withContext(NonCancellable) { acquire() }

  val result = try {
    use(acquired)
  } catch (e: CancellationException) {
    runReleaseAndRethrow(e) { release(acquired, ExitCase.Cancelled(e)) }
  } catch (t: Throwable) {
    runReleaseAndRethrow(t.nonFatalOrThrow()) { release(acquired, ExitCase.Failure(t)) }
  }

  withContext(NonCancellable) { release(acquired, ExitCase.Completed) }

  return result
}

@PublishedApi internal suspend inline fun runReleaseAndRethrow(
  original: Throwable,
  crossinline block: suspend () -> Unit
): Nothing {
  try {
    withContext(NonCancellable) { block() }
  } catch (e: Throwable) {
    original.addSuppressed(e.nonFatalOrThrow())
  }

  throw original
}
