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
import arrow.fx.coroutines.onCancel
import com.ivianuu.essentials.Disposable
import com.ivianuu.essentials.LocalScope
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.safeAs
import com.ivianuu.injekt.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation

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
