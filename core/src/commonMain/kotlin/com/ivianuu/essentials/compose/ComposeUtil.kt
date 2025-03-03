package com.ivianuu.essentials.compose

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable fun RestartableScope(block: @Composable () -> Unit) {
  block()
}

@Composable fun <T> produceScopedState(
  initialValue: T,
  vararg keys: Any?,
  producer: suspend ProduceStateScope<T>.() -> Unit
): State<T> {
  val result = rememberScoped { mutableStateOf(initialValue) }
  LaunchedScopedEffect(keys = keys) {
    producer(
      object : ProduceStateScope<T>, MutableState<T> by result, CoroutineScope by this {
        override suspend fun awaitDispose(onDispose: () -> Unit) =
          awaitCancellation()
      }
    )
  }
  return result
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun <T> StateFlow<T>.collectAsScopedState(
  context: CoroutineContext = EmptyCoroutineContext
): State<T> = collectAsScopedState(value, context)

@Composable fun <T : R, R> Flow<T>.collectAsScopedState(
  initial: R,
  context: CoroutineContext = EmptyCoroutineContext,
): State<R> = produceScopedState(initial, this, context) {
  if (context == EmptyCoroutineContext) {
    collect { value = it }
  } else withContext(context) { collect { value = it } }
}
