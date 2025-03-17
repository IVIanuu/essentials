package essentials.compose

import androidx.compose.runtime.*
import app.cash.molecule.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

fun <T> moleculeFlow(
  context: CoroutineContext = AndroidUiDispatcher.Main,
  block: @Composable () -> T
): Flow<T> =
  moleculeFlow(RecompositionMode.ContextClock, block)
    .flowOn(context)

fun launchMolecule(
  mode: RecompositionMode = RecompositionMode.ContextClock,
  context: CoroutineContext = AndroidUiDispatcher.Main,
  scope: CoroutineScope = inject,
  block: @Composable () -> Unit
): Job {
  val job = Job(scope.coroutineContext.job)
  (scope + job).launchMolecule(mode, {}, context, body = block)
  return job
}

fun <T> moleculeState(
  mode: RecompositionMode = RecompositionMode.ContextClock,
  context: CoroutineContext = AndroidUiDispatcher.Main,
  scope: CoroutineScope = inject,
  body: @Composable () -> T,
): State<T> {
  var state: MutableState<T>? = null

  scope.launchMolecule(
    context = context,
    mode = mode,
    emitter = { value ->
      val outputState = state
      if (outputState != null) {
        launch { outputState.value = value }
      } else {
        state = mutableStateOf(value)
      }
    },
    body = body,
  )

  return state!!
}
