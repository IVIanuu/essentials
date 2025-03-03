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

fun <T> CoroutineScope.moleculeStateFlow(
  context: CoroutineContext = AndroidUiDispatcher.Main,
  block: @Composable () -> T,
): StateFlow<T> = launchMolecule(RecompositionMode.Immediate, context, body = block)
