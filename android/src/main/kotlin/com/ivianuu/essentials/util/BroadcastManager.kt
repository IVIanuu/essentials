/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.*
import androidx.compose.runtime.*
import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Provide @Scoped<AppScope> class BroadcastManager(
  private val appContext: AppContext,
  private val appScope: Scope<AppScope>,
  private val broadcastScopeFactory: (BroadcastManager, @Service<BroadcastScope> Intent) -> Scope<BroadcastScope>,
  private val coroutineContexts: CoroutineContexts,
  private val logger: Logger
) {
  private val explicitBroadcasts = EventFlow<Intent>()

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

  internal fun onReceive(intent: Intent) {
    logger.d { "on receive $intent" }
    val broadcastScope = broadcastScopeFactory(this, intent)
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

        explicitBroadcasts.emit(intent)

        snapshotFlow { broadcastWorkerManager.state }.first { it == ScopeWorkerManager.State.COMPLETED }
      } finally {
        broadcastScope.dispose()
      }
    }
  }
}

@Provide @AndroidComponent class EsBroadcastReceiver(
  private val broadcastManager: BroadcastManager
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    broadcastManager.onReceive(intent)
  }
}

data object BroadcastScope
