package com.ivianuu.essentials.compose

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import injekt.*
import injekt.common.*
import kotlinx.coroutines.*

@Composable inline fun scopedAction(crossinline block: suspend () -> Unit): () -> Unit =
  action(LocalScope.current.coroutineScope, block)

@Composable inline fun <P1> scopedAction(crossinline block: suspend (P1) -> Unit): (P1) -> Unit =
  action(LocalScope.current.coroutineScope, block)

@Composable inline fun <P1, P2> scopedAction(crossinline block: suspend (P1, P2) -> Unit): (P1, P2) -> Unit =
  action(LocalScope.current.coroutineScope, block)

@Composable inline fun <P1, P2, P3> scopedAction(
  crossinline block: suspend (P1, P2, P3) -> Unit
): (P1, P2, P3) -> Unit = action(LocalScope.current.coroutineScope, block)

@Composable inline fun <P1, P2, P3, P4> scopedAction(
  crossinline block: suspend (P1, P2, P3, P4) -> Unit
): (P1, P2, P3, P4) -> Unit = action(LocalScope.current.coroutineScope, block)

@Composable inline fun <P1, P2, P3, P4, P5> scopedAction(
  crossinline block: suspend (P1, P2, P3, P4, P5) -> Unit
): (P1, P2, P3, P4, P5) -> Unit = action(LocalScope.current.coroutineScope, block)

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

      override fun onAbandoned() {
        job?.cancel()
        job = null
      }
    }
  }
}

@Composable fun <T : Any> rememberScoped(
  vararg keys: Any?,
  key: String? = null,
  init: () -> T,
): T {
  val scope = LocalScope.current
  val compositeKey = currentCompositeKeyHash
  // key is the one provided by the user or the one generated by the compose runtime
  val finalKey = if (!key.isNullOrEmpty()) key
  else compositeKey.toString(36)
  val holder = remember(scope, finalKey) { scope.scoped(finalKey) { ScopedHolder() } }

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
