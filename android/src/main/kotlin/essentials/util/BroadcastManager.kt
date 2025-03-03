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
import kotlin.time.Duration.Companion.milliseconds

@Stable @Provide @Scoped<AppScope> class BroadcastManager(
  private val appContext: AppContext,
  private val coroutineContexts: CoroutineContexts
) {
  internal val explicitBroadcasts = EventFlow<Intent>()

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
  private val appScope: Scope<AppScope>,
  private val broadcastManager: BroadcastManager,
  private val broadcastScopeFactory: (@Service<BroadcastScope> Intent) -> Scope<BroadcastScope>,
  private val logger: Logger
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    logger.d { "on receive $intent" }
    val broadcastScope = broadcastScopeFactory(intent)
    broadcastScope.coroutineScope.launch {
      try {
        val broadcastWorkerManager = broadcastScope.service<ScopeWorkerManager<BroadcastScope>>()

        par(
          {
            snapshotFlow { appScope.service<ScopeWorkerManager<AppScope>>().state }.first {
              it != ScopeWorkerManager.State.IDLE
            }
          },
          {
            snapshotFlow { broadcastWorkerManager.state }.first {
              it != ScopeWorkerManager.State.IDLE
            }
          }
        )

        // todo find a better way
        delay(100.milliseconds)

        broadcastManager.explicitBroadcasts.emit(intent)

        snapshotFlow { broadcastWorkerManager.state }
          .first { it == ScopeWorkerManager.State.COMPLETED }
      } finally {
        broadcastScope.dispose()
      }
    }
  }
}

data object BroadcastScope
