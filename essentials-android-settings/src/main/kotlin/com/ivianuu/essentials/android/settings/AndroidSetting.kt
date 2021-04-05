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

package com.ivianuu.essentials.android.settings

import android.provider.Settings
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.coroutines.actAndReply
import com.ivianuu.essentials.coroutines.actor
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.util.ContentChangesFactory
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface AndroidSetting<T> : Flow<T> {
    suspend fun update(reducer: T.() -> T): T
    fun dispatchUpdate(reducer: T.() -> T)
}

class AndroidSettingModule<T : S, S>(
    private val name: String,
    private val type: AndroidSettingsType
) {
    @Given
    fun setting(
        @Given scope: ScopeCoroutineScope<AppGivenScope>,
        @Given adapter: AndroidSettingAdapter<T>,
        @Given contentChangesFactory: ContentChangesFactory,
        @Given dispatcher: IODispatcher
    ): @Scoped<AppGivenScope> AndroidSetting<T> {
        val contentFlow = flow {
            contentChangesFactory(
                when (type) {
                    AndroidSettingsType.GLOBAL -> Settings.Global.getUriFor(name)
                    AndroidSettingsType.SECURE -> Settings.Secure.getUriFor(name)
                    AndroidSettingsType.SYSTEM -> Settings.System.getUriFor(name)
                }
            )
                .onStart { emit(Unit) }
                .map {
                    withContext(dispatcher) {
                        adapter.get()
                    }
                }
                .let { emitAll(it) }
        }.shareIn(scope, SharingStarted.Lazily, 1).distinctUntilChanged()
        return object : AndroidSetting<T>, Flow<T> by contentFlow {
            private val updateActor = scope.actor(Channel.UNLIMITED)
            override suspend fun update(reducer: T.() -> T): T = updateActor.actAndReply {
                val currentValue = adapter.get()
                val newValue = reducer(currentValue)
                if (currentValue != newValue) adapter.set(newValue)
                newValue
            }

            override fun dispatchUpdate(reducer: T.() -> T) {
                scope.launch { update(reducer) }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Given
    fun adapter(
        @Given adapterFactory: (@Given String, @Given AndroidSettingsType, @Given S) -> AndroidSettingAdapter<S>,
        @Given initial: @Initial T
    ): AndroidSettingAdapter<T> = adapterFactory(name, type, initial) as AndroidSettingAdapter<T>
}

enum class AndroidSettingsType {
    GLOBAL, SECURE, SYSTEM
}
