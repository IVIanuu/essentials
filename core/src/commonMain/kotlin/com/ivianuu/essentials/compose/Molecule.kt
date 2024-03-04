package com.ivianuu.essentials.compose

import androidx.compose.runtime.*
import app.cash.molecule.*
import com.ivianuu.essentials.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

fun <T> moleculeFlow(block: @Composable () -> T): Flow<T> = 
  moleculeFlow(RecompositionMode.Immediate, block)

fun <T> CoroutineScope.launchMolecule(
  mode: RecompositionMode = RecompositionMode.Immediate,
  emitter: (T) -> Unit = {},
  context: CoroutineContext = EmptyCoroutineContext,
  block: @Composable () -> T
): Job {
  val job = Job(coroutineContext.job)
  childCoroutineScope(job).launchMolecule(mode, emitter, context, body = block)
  return job
}

fun <T> CoroutineScope.launchMolecule(
  context: CoroutineContext = EmptyCoroutineContext,
  block: @Composable () -> T,
): StateFlow<T> = launchMolecule(RecompositionMode.Immediate, context, block)
