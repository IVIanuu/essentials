package com.ivianuu.essentials.compose

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import kotlinx.coroutines.*

@Composable fun LaunchedScopedEffect(
  vararg keys: Any?,
  block: suspend CoroutineScope.() -> Unit
) {
  val coroutineScope = scopedCoroutineScope
  rememberScoped(keys = keys) {
    object : RememberObserver {
      private var job: Job? = null

      override fun onRemembered() {
        job = coroutineScope.launch(block = block)
      }

      override fun onForgotten() {
        job?.cancel()
        job = null
      }

      override fun onAbandoned() = TODO()
    }
  }
}

@Composable fun <T : Any> rememberScoped(
  vararg keys: Any?,
  init: () -> T,
): T {
  val scope = LocalScope.current
  val key = currentCompositeKeyHash
  val holder = remember(scope, key) { scope.scoped(key) { ScopedHolder() } }

  val value = (holder.value
    .takeIf { it !== Uninitialized && keys.contentEquals(holder.keys) }
    ?: init().also { holder.updateValue(it, keys) })
    .unsafeCast<T>()

  SideEffect {
    if (!holder.rememberedValue) {
      holder.rememberedValue = true
      value.safeAs<RememberObserver>()?.onRemembered()
    }
  }

  return value
}

private class ScopedHolder : Disposable {
  var value: Any? = Uninitialized
  var keys: Array<out Any?>? = null
  var rememberedValue = false

  override fun dispose() {
    updateValue(Uninitialized, null)
  }

  fun updateValue(newValue: Any?, newKeys: Array<out Any?>?) {
    value.safeAs<Disposable>()?.dispose()
    value.safeAs<RememberObserver>()?.onForgotten()
    rememberedValue = false
    value = newValue
    keys = newKeys
  }
}

private val Uninitialized = Any()
