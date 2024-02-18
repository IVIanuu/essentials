package com.ivianuu.essentials.compose

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import kotlinx.coroutines.*

@Composable inline fun action(
  scope: CoroutineScope = rememberCoroutineScope(),
  crossinline block: suspend () -> Unit
): () -> Unit = { scope.launch { block() } }

@Composable inline fun <P1> action(
  scope: CoroutineScope = rememberCoroutineScope(),
  crossinline block: suspend (P1) -> Unit
): (P1) -> Unit = { p1 -> scope.launch { block(p1) } }

@Composable inline fun <P1, P2> action(
  scope: CoroutineScope = rememberCoroutineScope(),
  crossinline block: suspend (P1, P2) -> Unit
): (P1, P2) -> Unit = { p1, p2 -> scope.launch { block(p1, p2) } }

@Composable inline fun <P1, P2, P3> action(
  scope: CoroutineScope = rememberCoroutineScope(),
  crossinline block: suspend (P1, P2, P3) -> Unit
): (P1, P2, P3) -> Unit = { p1, p2, p3 -> scope.launch { block(p1, p2, p3) } }

@Composable inline fun <P1, P2, P3, P4> action(
  scope: CoroutineScope = rememberCoroutineScope(),
  crossinline block: suspend (P1, P2, P3, P4) -> Unit
): (P1, P2, P3, P4) -> Unit = { p1, p2, p3, p4 -> scope.launch { block(p1, p2, p3, p4) } }

@Composable inline fun <P1, P2, P3, P4, P5> action(
  scope: CoroutineScope = rememberCoroutineScope(),
  crossinline block: suspend (P1, P2, P3, P4, P5) -> Unit
): (P1, P2, P3, P4, P5) -> Unit = { p1, p2, p3, p4, p5 ->
  scope.launch { block(p1, p2, p3, p4, p5) }
}

@Composable inline fun scopedAction(crossinline block: suspend () -> Unit): () -> Unit =
  action(LocalScope.current.coroutineScope, block)

@Composable inline fun <P1> scopedAction(crossinline block: suspend (P1) -> Unit): (P1) -> Unit =
  action(LocalScope.current.coroutineScope, block)

@Composable inline fun <P1, P2> scopedAction(crossinline block: suspend (P1, P2) -> Unit): (P1, P2) -> Unit =
  action(LocalScope.current.coroutineScope, block)

@Composable inline fun <P1, P2, P3> scopedAction(
  crossinline block: suspend (P1, P2, P3) -> Unit
): (P1, P2, P3) -> Unit = action(LocalScope.current.coroutineScope, block)

@Composable inline fun <P1, P2, P3, P4> scopedAction(
  crossinline block: suspend (P1, P2, P3, P4) -> Unit
): (P1, P2, P3, P4) -> Unit = action(LocalScope.current.coroutineScope, block)

@Composable inline fun <P1, P2, P3, P4, P5> scopedAction(
  crossinline block: suspend (P1, P2, P3, P4, P5) -> Unit
): (P1, P2, P3, P4, P5) -> Unit = action(LocalScope.current.coroutineScope, block)
