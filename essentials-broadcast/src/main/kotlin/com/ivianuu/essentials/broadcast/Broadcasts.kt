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
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.coroutines.offerSafe
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.android.AppContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

@GivenFun
fun broadcasts(
    action: String,
    @Given appContext: AppContext,
    @Given mainDispatcher: MainDispatcher
): Flow<Intent> = callbackFlow<Intent> {
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            offerSafe(intent)
        }
    }
    appContext.registerReceiver(
        broadcastReceiver,
        IntentFilter().apply {
            addAction(action)
        }
    )
    awaitClose {
        runKatching {
            appContext.unregisterReceiver(broadcastReceiver)
        }
    }
}.flowOn(mainDispatcher)
