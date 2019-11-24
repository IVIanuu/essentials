/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.messaging

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ivianuu.injekt.Factory
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * A factory for broadcast receiver observables
 */
@Factory
class BroadcastFactory(private val context: Context) {

    fun create(vararg actions: String): Flow<Intent> = create(
        IntentFilter().apply {
            actions.forEach { addAction(it) }
        }
    )

    fun create(intentFilter: IntentFilter): Flow<Intent> = callbackFlow {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                offer(intent)
            }
        }

        try {
            context.registerReceiver(broadcastReceiver, intentFilter)
        } catch (e: Exception) {
        }

        awaitClose {
            try {
                context.unregisterReceiver(broadcastReceiver)
            } catch (e: Exception) {
            }
        }
    }

}