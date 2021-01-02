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
import com.ivianuu.essentials.android.settings.AndroidSettingAction.*
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.util.contentChanges
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

fun <S> androidSettingStateBinding(name: String, type: AndroidSettingsType): @Given (
    @Given GlobalScope,
    @Given IODispatcher,
    @Given (String, AndroidSettingsType, S) -> AndroidSettingsAdapter<S>,
    @Given contentChanges,
    @Given @Initial S,
    @Given Actions<AndroidSettingAction<S>>
) -> StateFlow<S> = { scope, dispatcher, adapterFactory, contentChanges, initial, actions ->
    @Suppress("UNCHECKED_CAST")
    scope.childCoroutineScope(dispatcher).state(initial) {
        val adapter = adapterFactory(name, type, initial)
        contentChanges(
            when (type) {
                AndroidSettingsType.GLOBAL -> Settings.Global.getUriFor(name)
                AndroidSettingsType.SECURE -> Settings.Secure.getUriFor(name)
                AndroidSettingsType.SYSTEM -> Settings.System.getUriFor(name)
            }
        )
            .onStart { emit(Unit) }
            .map { adapter.get() }
            .reduce { it }
            .launchIn(this)
        actions
            .filterIsInstance<Update<S>>()
            .onEach { action ->
                adapter.set(action.reducer(adapter.get()))
            }
            .launchIn(this)
    }
}

sealed class AndroidSettingAction<T> {
    data class Update<T>(
        val reducer: T.() -> T,
        val result: CompletableDeferred<T>?,
    ) : AndroidSettingAction<T>()
}

@GivenFun suspend fun <T> updateAndroidSetting(
    @Given dispatch: DispatchAction<AndroidSettingAction<T>>,
    reducer: T.() -> T,
): T {
    val result = CompletableDeferred<T>()
    dispatch(Update(reducer, result))
    return result.await()
}

@GivenFun fun <T> dispatchAndroidSettingUpdate(
    @Given dispatch: DispatchAction<AndroidSettingAction<T>>,
    reducer: T.() -> T,
) {
    dispatch(Update(reducer, null))
}
