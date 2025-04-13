package essentials.compose

import androidx.compose.runtime.*
import app.cash.molecule.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

fun <T> moleculeFlow(
  mode: RecompositionMode = inject,
  snapshotNotifier: SnapshotNotifier = inject,
  block: @Composable () -> T
): Flow<T> = moleculeFlow(mode, snapshotNotifier, body = block)

fun launchMolecule(
  context: MoleculeCoroutineContext = inject,
  mode: RecompositionMode = inject,
  snapshotNotifier: SnapshotNotifier = inject,
  scope: CoroutineScope = inject,
  block: @Composable () -> Unit
): Job {
  val job = Job(scope.coroutineContext.job)
  (scope + job).launchMolecule(mode, context, snapshotNotifier, block)
  return job
}

fun <T> moleculeState(
  context: MoleculeCoroutineContext = inject,
  mode: RecompositionMode = inject,
  snapshotNotifier: SnapshotNotifier = inject,
  scope: CoroutineScope = inject,
  body: @Composable () -> T,
): State<T> {
  var state: MutableState<T>? = null

  scope.launchMolecule(
    context = context,
    mode = mode,
    snapshotNotifier = snapshotNotifier,
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

@Tag typealias MoleculeCoroutineContext = CoroutineContext

@Provide object MoleculeDefaults {
  @Provide val context: MoleculeCoroutineContext get() = AndroidUiDispatcher.Main
  @Provide val recompositionMode get() = RecompositionMode.ContextClock
  @Provide val snapshotNotifier get() = SnapshotNotifier.External
}
