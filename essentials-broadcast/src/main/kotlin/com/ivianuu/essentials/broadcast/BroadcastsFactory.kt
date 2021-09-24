/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.catch
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.MainDispatcher
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

typealias BroadcastsFactory = (String) -> Flow<Intent>

@Provide fun broadcastsFactory(
  context: AppContext,
  mainDispatcher: MainDispatcher,
  scope: NamedCoroutineScope<AppScope>
): @Scoped<AppScope> BroadcastsFactory {
  val flowsByAction = mutableMapOf<String, Flow<Intent>>()
  val mutex = Mutex()

  return { action ->
    flow<Intent> {
      val inner = mutex.withLock {
        flowsByAction.getOrPut(action) {
          callbackFlow<Intent> {
            val broadcastReceiver = object : BroadcastReceiver() {
              override fun onReceive(context: Context, intent: Intent) {
                trySend(intent)
              }
            }
            context.registerReceiver(broadcastReceiver, IntentFilter(action))
            awaitClose {
              catch {
                context.unregisterReceiver(broadcastReceiver)
              }
            }
          }
            .flowOn(mainDispatcher)
            .shareIn(
              scope,
              SharingStarted.WhileSubscribed(),
              0
            )
        }
      }

      emitAll(inner)
    }
  }
}
