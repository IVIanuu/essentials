package com.ivianuu.essentials.compose

import androidx.compose.runtime.*
import app.cash.molecule.*
import com.ivianuu.essentials.coroutines.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

fun <T> CoroutineScope.launchMolecule(
  mode: RecompositionMode = RecompositionMode.Immediate,
  emitter: (T) -> Unit = {},
  context: CoroutineContext = EmptyCoroutineContext,
  block: @Composable () -> T
): Job {
  val job = Job(coroutineContext.job)
  childCoroutineScope(job).launchMolecule(mode = mode, emitter = emitter, context = context, body = block)
  return job
}
