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
import com.ivianuu.essentials.android.settings.AndroidSettingAction.Update
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.util.ContentChangesFactory
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AndroidSettingStateModule<T : S, S>(
    private val name: String,
    private val type: AndroidSettingsType
) {
    @Given
    fun settingsState(
        @Given scope: ScopeCoroutineScope<AppGivenScope>,
        @Given adapter: AndroidSettingsAdapter<T>,
        @Given contentChangesFactory: ContentChangesFactory,
        @Given dispatcher: IODispatcher
    ): @Scoped<AppGivenScope> Flow<T> = flow {
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
    }.shareIn(scope, SharingStarted.Lazily, 1)
        .distinctUntilChanged()

    @Given
    val actions: @Scoped<AppGivenScope> MutableSharedFlow<AndroidSettingAction<T>>
        get() = EventFlow()

    @Given
    fun collector(
        @Given scope: ScopeCoroutineScope<AppGivenScope>,
        @Given adapter: AndroidSettingsAdapter<T>,
        @Given dispatcher: IODispatcher
    ): @Scoped<AppGivenScope> Collector<AndroidSettingAction<T>> = { action ->
        scope.launch(dispatcher) {
            when (action) {
                is Update -> {
                    val newValue = action.reducer(adapter.get())
                    adapter.set(newValue)
                    action.result?.complete(newValue)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Given
    fun adapter(
        @Given adapterFactory: (@Given String, @Given AndroidSettingsType, @Given S) -> AndroidSettingsAdapter<S>,
        @Given initial: @Initial T
    ): AndroidSettingsAdapter<T> = adapterFactory(name, type, initial) as AndroidSettingsAdapter<T>
}

enum class AndroidSettingsType {
    GLOBAL, SECURE, SYSTEM
}

sealed class AndroidSettingAction<T> {
    data class Update<T>(
        val result: CompletableDeferred<T>? = null,
        val reducer: T.() -> T
    ) : AndroidSettingAction<T>()
}

suspend fun <T : Any> Collector<AndroidSettingAction<T>>.update(reducer: T.() -> T): T {
    val result = CompletableDeferred<T>()
    this(Update(result, reducer))
    return result.await()
}

fun <T : Any> Collector<AndroidSettingAction<T>>.dispatchUpdate(reducer: T.() -> T) {
    this(Update(reducer = reducer))
}
