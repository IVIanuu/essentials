package com.ivianuu.essentials.compose

import androidx.compose.runtime.*
import com.ivianuu.essentials.*

@Composable fun <T : Any> rememberScoped(
  vararg keys: Any?,
  key: Any? = null,
  init: () -> T
): T {
  val finalKey = key ?: currentCompositeKeyHash

  val scope = LocalScope.current
  val valueHolder = remember(scope, finalKey) {
    scope.scoped(finalKey) { ScopedValueHolder() }
  }

  val value = remember(*keys) {
    valueHolder.value
      .takeIf { it !== Uninitialized && keys.contentEquals(valueHolder.keys) }
      ?: init()
        .also {
          valueHolder.value.safeAs<Disposable>()?.dispose()
          valueHolder.value = it
          valueHolder.keys = keys
        }
  }

  return value as T
}

private class ScopedValueHolder : Disposable {
  var value: Any? = Uninitialized
  var keys: Array<out Any?> = emptyArray()

  override fun dispose() {
    value.safeAs<Disposable>()?.dispose()
    value = null
  }
}

private val Uninitialized = Any()
