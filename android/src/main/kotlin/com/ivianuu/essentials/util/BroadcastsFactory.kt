/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.flowOn

@Provide class BroadcastsFactory(
  private val appContext: AppContext,
  private val coroutineContexts: CoroutineContexts
) {
  operator fun invoke(vararg actions: String): Flow<Intent> = callbackFlow {
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
}
