/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.core

import androidx.activity.ComponentActivity
import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.AndroidComposeViewAmbient
import androidx.ui.core.CoroutineContextAmbient
import androidx.ui.core.FocusManagerAmbient
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.common.MultiAmbientProvider
import com.ivianuu.essentials.ui.common.with
import com.ivianuu.essentials.ui.coroutines.ProvideCoroutineScope
import com.ivianuu.essentials.ui.coroutines.coroutineScope
import com.ivianuu.essentials.ui.injekt.ComponentAmbient
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.ModuleBuilder
import com.ivianuu.injekt.Name

@Composable
fun Environment(
    activity: ComponentActivity,
    component: Component,
    retainedObjects: RetainedObjects,
    children: @Composable() () -> Unit
) {
    val composeView = AndroidComposeViewAmbient.current
    val focusManager = FocusManagerAmbient.current
    val coroutineScope = coroutineScope()
    MultiAmbientProvider(
        ActivityAmbient with activity,
        ComponentAmbient with component,
        CoroutineContextAmbient with coroutineScope.coroutineContext,
        KeyboardManagerAmbient with remember {
            KeyboardManager(focusManager, composeView, component.get())
        },
        RetainedObjectsAmbient with retainedObjects
    ) {
        ProvideCoroutineScope(coroutineScope = coroutineScope) {
            WindowInsetsManager {
                SystemBarManager {
                    ConfigurationFix {
                        OrientationProvider {
                            val uiInitializers = inject<Map<String, UiInitializer>>(name = UiInitializers)
                            uiInitializers.entries
                                .map { (key, initializer) ->
                                    { children: @Composable() () -> Unit ->
                                        d { "apply ui initializer $key" }
                                        initializer.apply(children)
                                    }
                                }
                                .fold(children) { current, initializer ->
                                    { initializer(current) }
                                }.invoke()
                        }
                    }
                }
            }
        }
    }
}

interface UiInitializer {
    @Composable
    fun apply(children: @Composable() () -> Unit)
}

@Name
annotation class UiInitializers {
    companion object
}

inline fun <reified T : UiInitializer> ModuleBuilder.bindUiInitializer(
    name: Any? = null
) {
    withBinding<T>(name) { bindUiInitializer() }
}

inline fun <reified T : UiInitializer> BindingContext<T>.bindUiInitializer(): BindingContext<T> {
    intoMap<String, UiInitializer>(
        entryKey = T::class.java.name,
        mapName = UiInitializers
    )
    return this
}

val EsUiInitializersModule = Module {
    map<String, UiInitializer>(mapName = UiInitializers)
}
