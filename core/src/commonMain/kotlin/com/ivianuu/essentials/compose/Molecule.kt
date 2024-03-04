package com.ivianuu.essentials.compose

import androidx.compose.runtime.*
import app.cash.molecule.*
import com.ivianuu.essentials.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

fun <T> moleculeFlow(block: @Composable () -> T): Flow<T> = 
  moleculeFlow(RecompositionMode.Immediate, block)

fun CoroutineScope.launchMolecule(
  mode: RecompositionMode = RecompositionMode.Immediate,
  context: CoroutineContext = EmptyCoroutineContext,
  block: @Composable () -> Unit
): Job {
  val job = Job(coroutineContext.job)
  childCoroutineScope(job).launchMolecule(mode, {}, context, body = block)
  return job
}

fun <T> CoroutineScope.moleculeStateFlow(
  context: CoroutineContext = EmptyCoroutineContext,
  block: @Composable () -> T,
): StateFlow<T> = launchMolecule(RecompositionMode.Immediate, context, body = block)
