/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.catch
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.MainContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

fun interface BroadcastsFactory {
  operator fun invoke(vararg actions: String): Flow<Intent>
}

@Provide fun broadcastsFactory(
  appContext: AppContext,
  coroutineContext: MainContext
) = BroadcastsFactory { actions ->
  callbackFlow<Intent> {
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
    .flowOn(coroutineContext)
}
