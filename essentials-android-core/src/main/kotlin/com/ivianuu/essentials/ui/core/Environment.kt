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

import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.remember
import androidx.ui.core.CoroutineContextAmbient
import androidx.ui.core.FocusManagerAmbient
import androidx.ui.core.OwnerAmbient
import com.ivianuu.essentials.ui.coroutines.ProvideCoroutineScope
import com.ivianuu.essentials.ui.coroutines.coroutineScope
import com.ivianuu.essentials.ui.injekt.ComponentAmbient
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.QualifierMarker
import com.ivianuu.injekt.common.map
import com.ivianuu.injekt.keyOf

@Composable
fun Environment(
    activity: ComponentActivity,
    component: Component,
    retainedObjects: RetainedObjects,
    children: @Composable () -> Unit
) {
    val ownerView = OwnerAmbient.current as View
    val focusManager = FocusManagerAmbient.current
    val coroutineScope = coroutineScope()
    Providers(
        ActivityAmbient provides activity,
        ComponentAmbient provides component,
        CoroutineContextAmbient provides coroutineScope.coroutineContext,
        KeyboardManagerAmbient provides remember {
            KeyboardManager(focusManager, ownerView, component.get())
        },
        RetainedObjectsAmbient provides retainedObjects
    ) {
        ProvideCoroutineScope(coroutineScope = coroutineScope) {
            WindowInsetsManager {
                SystemBarManager {
                    val uiInitializers =
                        inject<Map<String, UiInitializer>>(qualifier = UiInitializers)
                    val logger = inject<Logger>()
                    uiInitializers.entries
                        .map { (key, initializer) ->
                            val function: @Composable (@Composable () -> Unit) -> Unit =
                                { children ->
                                    logger.d("apply ui initializer $key")
                                    initializer.apply(children)
                                }
                            function
                        }
                        .fold(children) { current, initializer ->
                            { initializer(current) }
                        }.invoke()
                }
            }
        }
    }
}

interface UiInitializer {
    @Composable
    fun apply(children: @Composable () -> Unit)
}

@QualifierMarker
annotation class UiInitializers {
    companion object : Qualifier.Element
}

inline fun <reified T : UiInitializer> ComponentBuilder.bindUiInitializerIntoMap(
    initializerQualifier: Qualifier = Qualifier.None
) {
    map<String, UiInitializer>(mapQualifier = UiInitializers) {
        put(T::class.java.name, keyOf<T>(qualifier = initializerQualifier))
    }
}

@ApplicationScope
@Module
private fun ComponentBuilder.esUiInitializerInjectionModule() {
    map<String, UiInitializer>(mapQualifier = UiInitializers)
}
