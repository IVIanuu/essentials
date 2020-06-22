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

package com.ivianuu.essentials.app

import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.BindingEffect
import com.ivianuu.injekt.composition.BindingEffectFunction
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.map
import kotlin.reflect.KClass

/**
 * Will be instantiated on app start up
 * Can be used to initialize global stuff like logging
 *
 * ´´´
 * class AnalyticsInitializer : AppInitializer {
 *     init {
 *         Analytics.initialize(Logger())
 *     }
 * }
 * ´´´
 */
interface AppInitializer

@BindingEffect(ApplicationComponent::class)
annotation class BindAppInitializer

@BindingEffectFunction(BindAppInitializer::class)
@Module
inline fun <reified T : AppInitializer> appInitializer() {
    map<KClass<*>, AppInitializer> {
        put<T>(T::class)
    }
}

@Module
fun esAppInitializerModule() {
    installIn<ApplicationComponent>()
    map<KClass<*>, AppInitializer>()
}

@Transient
class AppInitRunner(
    private val logger: Logger,
    initializers: Map<KClass<*>, @Provider () -> AppInitializer>
) {
    init {
        initializers
            .forEach {
                logger.d(tag = "Init", message = "initialize ${it.key.java.name}")
                it.value()
            }
    }
}
