package com.ivianuu.essentials.coroutines

import arrow.fx.coroutines.*
import kotlinx.coroutines.*

suspend inline fun <A> bracketCase(
  crossinline acquire: suspend () -> A,
  crossinline release: suspend (A, ExitCase) -> Unit
): Nothing = bracketCase(acquire = acquire, use = { awaitCancellation() }, release = release)

suspend inline fun onCancel(crossinline onCancel: suspend () -> Unit): Nothing =
  onCancel(fa = { awaitCancellation() }, onCancel = onCancel)
