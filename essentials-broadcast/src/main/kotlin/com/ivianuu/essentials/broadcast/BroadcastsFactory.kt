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

import android.content.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

typealias BroadcastsFactory = (String) -> Flow<Intent>

@Given
fun broadcastsFactory(
    @Given appContext: AppContext,
    @Given mainDispatcher: MainDispatcher
): BroadcastsFactory = { action ->
    callbackFlow<Intent> {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                catch { offer(intent) }
            }
        }
        appContext.registerReceiver(broadcastReceiver, IntentFilter(action))
        awaitClose {
            catch {
                appContext.unregisterReceiver(broadcastReceiver)
            }
        }
    }.flowOn(mainDispatcher)
}
