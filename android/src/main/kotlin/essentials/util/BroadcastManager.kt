/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.content.*
import androidx.compose.runtime.*
import essentials.*
import essentials.app.*
import essentials.coroutines.*
import essentials.logging.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

@Composable fun <T> broadcastState(
  vararg actions: String,
  scope: Scope<*> = inject,
  compute: (Intent?) -> T
): T = produceState(remember { compute(null) }) {
  broadcastsOf(*actions).collect { value = compute(it) }
}.value

fun broadcastsOf(vararg actions: String, scope: Scope<*> = inject): Flow<Intent> = merge(
  explicitBroadcasts.filter { it.action in actions },
  callbackFlow {
    val broadcastReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        trySend(intent)
      }
    }
    appContext().registerReceiver(broadcastReceiver, IntentFilter().apply {
      actions.forEach { addAction(it) }
    })
    awaitClose {
      catch { appContext().unregisterReceiver(broadcastReceiver) }
    }
  }
    .flowOn(coroutineContexts().main)
)

private val explicitBroadcasts = EventFlow<Intent>()

@Provide @AndroidComponent class EsBroadcastReceiver(
  @property:Provide private val scope: Scope<AppScope> = inject
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    d { "on receive $intent" }
    coroutineScope().launch {
      delay(100)
      explicitBroadcasts.emit(intent)
    }
  }
}
