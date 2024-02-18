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
  key: Any? = null,
  init: () -> T
): T {
  val finalKey = key ?: currentCompositeKeyHash

  val scope = LocalScope.current
  val holder = remember(scope, finalKey) {
    scope.scoped(finalKey) { ScopedHolder() }
  }

  holder.value
    .takeIf { it !== Uninitialized && keys.contentEquals(holder.keys) }
    ?: init()
      .also {
        holder.value.safeAs<Disposable>()?.dispose()
        holder.value.safeAs<RememberObserver>()?.onForgotten()

        holder.value = it
        holder.keys = keys
        holder.rememberedValue = false
      }

  val value = holder.value.unsafeCast<T>()
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
    value.safeAs<Disposable>()?.dispose()
    value.safeAs<RememberObserver>()?.onForgotten()
    value = null
    keys = null
    rememberedValue = false
  }
}

private val Uninitialized = Any()
