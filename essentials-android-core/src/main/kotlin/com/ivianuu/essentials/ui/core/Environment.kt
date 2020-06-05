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

import androidx.compose.Composable
import androidx.compose.Providers
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.composition.BindingEffect
import com.ivianuu.injekt.composition.BindingEffectFunction
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.map
import kotlin.reflect.KClass

@Composable
fun Environment(
    retainedObjects: RetainedObjects,
    children: @Composable () -> Unit
) {
    Providers(RetainedObjectsAmbient provides retainedObjects) {
        WindowInsetsManager {
            SystemBarManager {
                val uiInitializers = inject<Map<KClass<out UiInitializer>, UiInitializer>>()
                val finalComposable: @Composable () -> Unit = uiInitializers.entries
                    .map { (key, initializer) ->
                        val function: @Composable (@Composable () -> Unit) -> Unit =
                            { children ->
                                inject<Logger>().d("apply ui initializer $key")
                                initializer.apply(children)
                            }
                        function
                    }
                    .fold(children) { current: @Composable () -> Unit,
                                      initializer: @Composable (@Composable () -> Unit) -> Unit ->
                        @Composable { initializer(current) }
                    }

                finalComposable()
            }
        }
    }
}

interface UiInitializer {
    @Composable
    fun apply(children: @Composable () -> Unit)
}

@BindingEffect(ActivityComponent::class)
annotation class BindUiInitializer

@BindingEffectFunction(BindUiInitializer::class)
@Module
inline fun <reified T : UiInitializer> bindUiInitializer() {
    map<KClass<out UiInitializer>, UiInitializer> {
        put<T>(T::class)
    }
}

@Module
fun esUiInitializerModule() {
    installIn<ApplicationComponent>()
    map<KClass<out UiInitializer>, UiInitializer>()
}
