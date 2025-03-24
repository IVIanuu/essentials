package essentials.compose

import android.annotation.*
import androidx.compose.runtime.*
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

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
  if (context == EmptyCoroutineContext) collect { value = it }
  else withContext(context) { collect { value = it } }
}

@Composable inline fun scopedAction(
  scope: Scope<*> = inject,
  crossinline block: suspend () -> Unit
): () -> Unit = action(block = block)

@Composable inline fun <P1> scopedAction(
  scope: Scope<*> = inject,
  crossinline block: suspend (P1) -> Unit
): (P1) -> Unit = action(block = block)

@Composable inline fun <P1, P2> scopedAction(
  scope: Scope<*> = inject,
  crossinline block: suspend (P1, P2) -> Unit
): (P1, P2) -> Unit = action(block = block)

@Composable fun LaunchedScopedEffect(
  vararg keys: Any?,
  scope: Scope<*> = inject,
  block: suspend (@Provide CoroutineScope) -> Unit
) {
  rememberScoped(inputs = keys) {
    object : RememberObserver {
      private var job: Job? = null

      override fun onRemembered() {
        job = launch(
          // TODO REMOVE EXPLICIT PASSING ONCE WE FIGURED OUT THAT NASTY INJEKT BUG
          scope = CoroutineScopeProviders.scopeCoroutineScope(scope),
          block = block
        )
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
  vararg inputs: Any?,
  scope: Scope<*> = inject,
  key: Any? = null,
  init: () -> T,
): T {
  val compositeKey = currentCompositeKeyHash
  val finalKey = key ?: compositeKey.toString(36)

  val holder = remember(scope, finalKey) {
    scope.scoped(finalKey) { ScopedHolder() }
  }

  var value = holder.getValueIfInputsAreEqual(inputs)
  if (value === Uninitialized) value = init()

  SideEffect { holder.updateValue(value, inputs) }

  return value.unsafeCast()
}

private class ScopedHolder : DisposableHandle {
  private var value: Any? = Uninitialized
  private var inputs: Array<*>? = null

  fun getValueIfInputsAreEqual(inputs: Array<*>?): Any? =
    if (inputs.contentEquals(this.inputs)) value
    else Uninitialized

  fun updateValue(value: Any?, inputs: Array<*>?) {
    if (inputs.contentEquals(this.inputs)) return

    this.value.safeAs<DisposableHandle>()?.dispose()
    this.value.safeAs<RememberObserver>()?.onForgotten()

    this.value = value
    this.inputs = inputs
    this.value.safeAs<RememberObserver>()?.onRemembered()
  }

  override fun dispose() {
    updateValue(Uninitialized, null)
  }
}

private val Uninitialized = Any()
