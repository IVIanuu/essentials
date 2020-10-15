/*
 * Copyright 2020 Manuel Wrage
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
import com.ivianuu.essentials.coroutines.offerSafe
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@FunBinding
fun broadcasts(
    applicationContext: ApplicationContext,
    action: @Assisted String,
): Flow<Intent> {
    return callbackFlow {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                offerSafe(intent)
            }
        }
        try {
            applicationContext.registerReceiver(
                broadcastReceiver,
                IntentFilter().apply {
                    addAction(action)
                }
            )
        } catch (t: Throwable) {
        }

        awaitClose {
            try {
                applicationContext.unregisterReceiver(broadcastReceiver)
            } catch (t: Throwable) {
            }
        }
    }
}
