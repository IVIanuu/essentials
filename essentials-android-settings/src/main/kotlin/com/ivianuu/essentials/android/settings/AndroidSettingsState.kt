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
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.util.ContentChangesFactory
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import kotlinx.coroutines.CompletableDeferred
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
    @Scoped<AppComponent>
    @Given
    operator fun invoke(
        @Given scope: GlobalScope,
        @Given dispatcher: IODispatcher,
        @Given adapterFactory: (@Given String, @Given AndroidSettingsType, @Given S) -> AndroidSettingsAdapter<S>,
        @Given contentChangesFactory: ContentChangesFactory,
        @Given initial: @Initial T,
        @Given actions: Actions<AndroidSettingAction<T>>,
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
}

internal typealias AndroidSettingsStateReady<T> = MutableStateFlow<Boolean>

@Scoped<AppComponent>
@Given
fun <T> androidSettingsStateReady(): AndroidSettingsStateReady<T> =
    MutableStateFlow(false)

sealed class AndroidSettingAction<T> {
    data class Update<T>(
        val reducer: T.() -> T,
        val result: CompletableDeferred<T>?,
    ) : AndroidSettingAction<T>()
}

typealias AndroidSettingUpdater<T> = suspend (T.() -> T) -> T

@Given
fun <T> androidSettingUpdater(
    @Given dispatch: DispatchAction<AndroidSettingAction<T>>,
    @Given ready: AndroidSettingsStateReady<T>,
    @Given state: StateFlow<T> // workaround to ensure that the state is initialized
): AndroidSettingUpdater<T> = { reducer ->
    ready.first { it }
    val result = CompletableDeferred<T>()
    dispatch(Update(reducer, result))
    result.await()
}

typealias AndroidSettingUpdateDispatcher<T> = (T.() -> T) -> Unit

@Given
fun <T> dispatchAndroidSettingUpdate(
    @Given dispatch: DispatchAction<AndroidSettingAction<T>>,
    @Given ready: AndroidSettingsStateReady<T>,
    @Given scope: GlobalScope,
    @Given state: StateFlow<T> // workaround to ensure that the state is initialized
): AndroidSettingUpdateDispatcher<T> = { reducer ->
    scope.launch {
        ready.first { it }
        dispatch(Update(reducer, null))
    }
}
