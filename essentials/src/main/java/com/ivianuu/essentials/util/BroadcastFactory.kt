/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ivianuu.injekt.codegen.Factory
import com.ivianuu.kommon.core.content.intentFilterOf
import com.ivianuu.rxjavaktx.observable
import io.reactivex.Observable

/**
 * A factory for broadcast receiver observables
 */
@Factory
class BroadcastFactory(private val context: Context) {

    fun create(vararg actions: String): Observable<Intent> = create(intentFilterOf(*actions))

    fun create(intentFilter: IntentFilter) = observable<Intent> { e ->
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (!e.isDisposed) {
                    e.onNext(intent)
                }
            }
        }

        e.setCancellable { context.unregisterReceiver(broadcastReceiver) }

        if (!e.isDisposed) {
            context.registerReceiver(broadcastReceiver, intentFilter)
        }
    }
}