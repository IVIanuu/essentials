package essentials.compose

import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*

inline fun action(
  scope: CoroutineScope = inject,
  crossinline block: suspend () -> Unit
): () -> Unit = { launch { block() } }

inline fun <P1> action(
  scope: CoroutineScope = inject,
  crossinline block: suspend (P1) -> Unit
): (P1) -> Unit = { p1 -> launch { block(p1) } }

inline fun <P1, P2> action(
  scope: CoroutineScope = inject,
  crossinline block: suspend (P1, P2) -> Unit
): (P1, P2) -> Unit = { p1, p2 -> launch { block(p1, p2) } }

inline fun <P1, P2, P3> action(
  scope: CoroutineScope = inject,
  crossinline block: suspend (P1, P2, P3) -> Unit
): (P1, P2, P3) -> Unit = { p1, p2, p3 -> launch { block(p1, p2, p3) } }
