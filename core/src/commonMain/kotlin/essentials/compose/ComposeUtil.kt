package essentials.compose

import android.annotation.*
import androidx.compose.runtime.*
import essentials.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

@Composable fun RestartableScope(block: @Composable () -> Unit) {
  block()
}

@Composable fun <T> produceScopedState(
  initialValue: T,
  vararg keys: Any?,
  scope: Scope<*> = inject,
  producer: suspend ProduceStateScope<T>.() -> Unit
): State<T> {
  val result = rememberScoped { mutableStateOf(initialValue) }
  LaunchedScopedEffect(keys = keys) {
    producer(
      object : ProduceStateScope<T>, MutableState<T> by result, CoroutineScope by implicitly() {
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
  context: CoroutineContext = EmptyCoroutineContext,
  scope: Scope<*> = inject
): State<T> = collectAsScopedState(value, context)

@Composable fun <T : R, R> Flow<T>.collectAsScopedState(
  initial: R,
  context: CoroutineContext = EmptyCoroutineContext,
  scope: Scope<*> = inject
): State<R> = produceScopedState(initial, this, context) {
  if (context == EmptyCoroutineContext) {
    collect { value = it }
  } else withContext(context) { collect { value = it } }
}
