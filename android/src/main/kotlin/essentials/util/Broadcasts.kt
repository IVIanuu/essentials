/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.content.*
import androidx.compose.runtime.*
import essentials.*
import essentials.app.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

@Composable fun <T> broadcastStateOf(
  vararg actions: String,
  context: Context = inject,
  compute: (Intent?) -> T
): T = produceState(remember { compute(null) }) {
  broadcastsOf(*actions).collect { value = compute(it) }
}.value

fun broadcastsOf(
  vararg actions: String,
  context: Context = inject
): Flow<Intent> = merge(
  explicitBroadcasts.filter { it.action in actions },
  callbackFlow {
    val broadcastReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        trySend(intent)
      }
    }
    context.registerReceiver(broadcastReceiver, IntentFilter().apply {
      actions.forEach { addAction(it) }
    })
    awaitClose {
      catch {
        context.unregisterReceiver(broadcastReceiver)
      }
    }
  }
)

private val explicitBroadcasts = EventFlow<Intent>()

@Provide @AndroidComponent class EsBroadcastReceiver(
  @property:Provide private val logger: Logger,
  private val scope: ScopedCoroutineScope<AppScope>
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    d { "on receive $intent" }
    scope.launch {
      delay(100)
      explicitBroadcasts.emit(intent)
    }
  }
}
