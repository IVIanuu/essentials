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
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.util.contentChanges
import com.ivianuu.injekt.Arg
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.Scoped
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
@Effect
annotation class AndroidSettingsStateBinding<T>(
    val name: String,
    val type: AndroidSettingsType,
) {
    companion object {
        @Scoped(ApplicationComponent::class)
        @Binding
        fun <@Arg("T") T, S : T> state(
            @Arg("name") name: String,
            @Arg("type") type: AndroidSettingsType,
            scope: GlobalScope,
            adapterFactory: (String, AndroidSettingsType, T) -> AndroidSettingsAdapter<T>,
            contentChanges: contentChanges,
            initial: @Initial S,
            actions: Actions<AndroidSettingAction<S>>,
        ): StateFlow<S> {
            @Suppress("UNCHECKED_CAST")
            return scope.state(initial) {
                val adapter = adapterFactory(name, type, initial) as AndroidSettingsAdapter<S>
                contentChanges(
                    when (type) {
                        AndroidSettingsType.GLOBAL -> Settings.Global.getUriFor(name)
                        AndroidSettingsType.SECURE -> Settings.Secure.getUriFor(name)
                        AndroidSettingsType.SYSTEM -> Settings.System.getUriFor(name)
                    }
                )
                    .onStart { emit(Unit) }
                    .map { adapter.get() }
                    .onEach { println("new value $it") }
                    .reduce { it }
                    .launchIn(this)
                actions
                    .filterIsInstance<AndroidSettingAction.Update<S>>()
                    .onEach { action ->
                        adapter.set(action.reducer(adapter.get()))
                    }
                    .launchIn(this)
            }
        }

        @Binding
        inline val <@Arg("T") T, S : T> StateFlow<S>.androidSettingsFlow: Flow<S>
            get() = this
    }
}

sealed class AndroidSettingAction<T> {
    data class Update<T>(val reducer: T.() -> T) : AndroidSettingAction<T>()
}

@FunBinding
fun <T> updateAndroidSetting(
    dispatch: DispatchAction<AndroidSettingAction<T>>,
    @FunApi reducer: T.() -> T,
) {
    dispatch(AndroidSettingAction.Update(reducer))
}
