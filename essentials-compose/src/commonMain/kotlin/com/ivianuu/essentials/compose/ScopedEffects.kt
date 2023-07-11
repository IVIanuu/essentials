/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProduceStateScope
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.ivianuu.essentials.Disposable
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.safeAs
import kotlinx.coroutines.CoroutineScope

val LocalScope = compositionLocalOf<Scope<*>> { error("No scope provided") }

@Composable fun <T : Any> rememberScoped(vararg inputs: Any?, key: Any? = null, init: () -> T): T {
  val scope = LocalScope.current

  val finalKey = key ?: currentCompositeKeyHash

  val valueHolder = remember { scope.scoped(finalKey) { ScopedValueHolder() } }

  val value = remember(*inputs) {
    valueHolder.value
      .takeIf { it !== Uninitialized && inputs.contentEquals(valueHolder.inputs) }
      ?: init()
        .also {
          valueHolder.value.safeAs<Disposable>()?.dispose()
          valueHolder.value = it
          valueHolder.inputs = inputs
        }
  }

  return value as T
}

private class ScopedValueHolder : Disposable {
  var value: Any? = Uninitialized
  var inputs: Array<out Any?> = emptyArray()

  override fun dispose() {
    value.safeAs<Disposable>()?.dispose()
    value = null
  }
}

private val Uninitialized = Any()

@Composable fun <T> produceScopedState(
  initialValue: T,
  vararg keys: Any?,
  producer: suspend ProduceStateScope<T>.() -> Unit
): State<T> {
  val state = rememberScoped { mutableStateOf(initialValue) }
  LaunchedEffect(keys) {
    producer(
      object : ProduceStateScope<T>, MutableState<T> by state, CoroutineScope by this {
        override suspend fun awaitDispose(onDispose: () -> Unit) =
          onCancel { onDispose() }
      }
    )
  }
  return state
}
