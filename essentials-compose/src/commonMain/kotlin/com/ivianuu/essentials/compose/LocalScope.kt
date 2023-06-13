/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.remember
import com.ivianuu.essentials.Scope

val LocalScope = compositionLocalOf<Scope<*>> { error("No scope provided") }

@Composable fun <T : Any> rememberScoped(vararg inputs: Any?, key: Any? = null, init: () -> T): T {
  val scope = LocalScope.current

  val finalKey = key ?: currentCompositeKeyHash

  val valueHolder = remember { scope.scoped(finalKey) { ScopedValueHolder() } }

  val value = remember(*inputs) {
    valueHolder.value
      .takeIf {
        it !== valueHolder &&
            inputs.contentEquals(valueHolder.inputs)
      }
      ?: init()
        .also {
          valueHolder.value = it
          valueHolder.inputs = inputs
        }
  }

  return value as T
}

private class ScopedValueHolder {
  var value: Any? = this
  var inputs: Array<out Any?> = emptyArray()
}
