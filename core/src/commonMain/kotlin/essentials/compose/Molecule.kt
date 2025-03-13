package essentials.compose

import androidx.compose.runtime.*
import app.cash.molecule.*
import essentials.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

fun <T> moleculeFlow(
  context: CoroutineContext = AndroidUiDispatcher.Main,
  block: @Composable () -> T
): Flow<T> =
  moleculeFlow(RecompositionMode.ContextClock, block)
    .flowOn(context)

fun CoroutineScope.launchMolecule(
  mode: RecompositionMode = RecompositionMode.ContextClock,
  context: CoroutineContext = AndroidUiDispatcher.Main,
  block: @Composable () -> Unit
): Job {
  val job = Job(coroutineContext.job)
  childCoroutineScope(job).launchMolecule(mode, {}, context, body = block)
  return job
}

fun <T> CoroutineScope.moleculeState(
  mode: RecompositionMode = RecompositionMode.ContextClock,
  context: CoroutineContext = AndroidUiDispatcher.Main,
  body: @Composable () -> T,
): State<T> {
  var state: MutableState<T>? = null

  launchMolecule(
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
