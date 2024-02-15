package com.ivianuu.essentials.coroutines

import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.bracketCase
import arrow.fx.coroutines.onCancel
import kotlinx.coroutines.awaitCancellation

suspend inline fun <A> bracketCase(
  crossinline acquire: suspend () -> A,
  crossinline release: suspend (A, ExitCase) -> Unit
): Nothing = bracketCase(acquire = acquire, use = { awaitCancellation() }, release = release)

suspend inline fun onCancel(crossinline onCancel: suspend () -> Unit): Nothing =
  onCancel(fa = { awaitCancellation() }, onCancel = onCancel)
