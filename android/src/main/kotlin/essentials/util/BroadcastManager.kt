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

@Stable @Provide @Scoped<AppScope> class BroadcastManager(
  private val appContext: AppContext,
  private val coroutineContexts: CoroutineContexts
) {
  internal val explicitBroadcasts = EventFlow<Intent>()

  @Composable fun <T> broadcastState(
    vararg actions: String,
    compute: (Intent?) -> T
  ): T = produceState(remember { compute(null) }) {
    broadcasts(*actions).collect { value = compute(it) }
  }.value

  fun broadcasts(vararg actions: String): Flow<Intent> = merge(
    explicitBroadcasts.filter { it.action in actions },
    callbackFlow {
      val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
          trySend(intent)
        }
      }
      appContext.registerReceiver(broadcastReceiver, IntentFilter().apply {
        actions.forEach { addAction(it) }
      })
      awaitClose {
        catch {
          appContext.unregisterReceiver(broadcastReceiver)
        }
      }
    }
      .flowOn(coroutineContexts.main)
  )
}

@Provide @AndroidComponent class EsBroadcastReceiver(
  private val broadcastManager: BroadcastManager,
  private val logger: Logger,
  private val scope: ScopedCoroutineScope<AppScope>
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    logger.d { "on receive $intent" }
    scope.launch {
      delay(100)
      broadcastManager.explicitBroadcasts.emit(intent)
    }
  }
}
