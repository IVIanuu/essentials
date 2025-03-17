/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.app.*
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

@Stable @Provide @Scoped<AppScope> class Broadcasts(
  private val context: Application,
  private val coroutineContexts: CoroutineContexts
) {
  internal val explicitBroadcasts = EventFlow<Intent>()

  @Composable fun <T> stateOf(
    vararg actions: String,
    compute: (Intent?) -> T
  ): T = produceState(remember { compute(null) }) {
    of(*actions).collect { value = compute(it) }
  }.value

  fun of(vararg actions: String): Flow<Intent> = merge(
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
      .flowOn(coroutineContexts.main)
  )
}

@Provide @AndroidComponent class EsBroadcastReceiver(
  private val broadcasts: Broadcasts,
  @property:Provide private val logger: Logger,
  private val scope: ScopedCoroutineScope<AppScope>
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    d { "on receive $intent" }
    scope.launch {
      delay(100)
      broadcasts.explicitBroadcasts.emit(intent)
    }
  }
}
