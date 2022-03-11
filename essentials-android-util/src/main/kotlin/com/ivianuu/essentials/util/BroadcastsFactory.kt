/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

fun interface BroadcastsFactory {
  operator fun invoke(vararg actions: String): Flow<Intent>
}

@Provide fun broadcastsFactory(
  context: AppContext,
  coroutineContext: MainContext
) = BroadcastsFactory { actions ->
  callbackFlow<Intent> {
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
    .flowOn(coroutineContext)
}
