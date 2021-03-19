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
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.util.ContentChangesFactory
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AndroidSettingStateModule<T : S, S>(
    private val name: String,
    private val type: AndroidSettingsType
) {
    @Suppress("UNCHECKED_CAST")
    @Scoped<AppGivenScope>
    @Given
    fun settingsState(
        @Given scope: ScopeCoroutineScope<AppGivenScope>,
        @Given dispatcher: IODispatcher,
        @Given adapterFactory: (@Given String, @Given AndroidSettingsType, @Given S) -> AndroidSettingsAdapter<S>,
        @Given contentChangesFactory: ContentChangesFactory,
        @Given initial: @Initial T,
        @Given actions: Flow<AndroidSettingAction<T>>,
        @Given ready: AndroidSettingsStateReady<T>
    ): StateFlow<T> = scope.childCoroutineScope(dispatcher).state(
        initial = initial,
        started = SharingStarted.Eagerly
    ) {
        val adapter = adapterFactory(name, type, initial)
        contentChangesFactory(
            when (type) {
                AndroidSettingsType.GLOBAL -> Settings.Global.getUriFor(name)
                AndroidSettingsType.SECURE -> Settings.Secure.getUriFor(name)
                AndroidSettingsType.SYSTEM -> Settings.System.getUriFor(name)
            }
        )
            .onStart { emit(Unit) }
            .map { adapter.get() as T }
            .reduce { it }
            .launchIn(this)
        actions
            .onStart { ready.value = true }
            .filterIsInstance<Update<S>>()
            .onEach { action ->
                val newValue = action.reducer(adapter.get())
                adapter.set(newValue)
                action.result?.complete(newValue)
            }
            .launchIn(this)
    }

    @Given
    val actions = EventFlow<AndroidSettingAction<T>>()

    @Scoped<AppGivenScope>
    @Given
    fun collector(
        @Given actions: MutableSharedFlow<AndroidSettingAction<T>>,
        @Suppress("UNUSED_PARAMETER") @Given state: StateFlow<T>, // inject to start state
        @Given ready: AndroidSettingsStateReady<T>,
        @Given scope: ScopeCoroutineScope<AppGivenScope>
    ): Collector<AndroidSettingAction<T>> = { action ->
        scope.launch {
            ready.first()
            actions.tryEmit(action)
        }
    }
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

internal typealias AndroidSettingsStateReady<T> = MutableStateFlow<Boolean>

@Scoped<AppGivenScope>
@Given
fun <T> androidSettingsStateReady(): AndroidSettingsStateReady<T> =
    MutableStateFlow(false)
