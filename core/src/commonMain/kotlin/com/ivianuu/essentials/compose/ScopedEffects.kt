package com.ivianuu.essentials.compose

import androidx.compose.runtime.*
import arrow.fx.coroutines.*
import com.ivianuu.essentials.*
import kotlinx.coroutines.*

@Composable fun <T : Any> rememberScoped(
  vararg inputs: Any?,
  key: Any? = null,
  init: () -> T
): T {
  val finalKey = key ?: currentCompositeKeyHash

  val scope = LocalScope.current
  val valueHolder = remember(scope, finalKey) {
    scope.scoped(finalKey) { ScopedValueHolder() }
  }

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
          onCancel({ awaitCancellation() }) { onDispose() }
      }
    )
  }
  return state
}
