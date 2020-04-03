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
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.QualifierMarker
import com.ivianuu.injekt.common.map
import com.ivianuu.injekt.eager

/**
 * Will be instantiated on app start up
 * Can be used to initialize global stuff like logging
 *
 * ´´´
 * class AnalyticsInitializer {
 *     init {
 *         Analytics.initialize(Logger())
 *     }
 * }
 * ´´´
 *
 * Must be bound inside your module like this:
 *
 * ´´´
 * fun ComponentBuilder.analyticsBindings() {
 *     bindAppInitializer<AnalyticsInitializer>()
 * }
 * ´´´
 *
 */
interface AppInitializer

inline fun <reified T : AppInitializer> ComponentBuilder.bindAppInitializerIntoMap(
    initializerQualifier: Qualifier = Qualifier.None
) {
    map<String, AppInitializer>(mapQualifier = AppInitializers) {
        put<T>(entryKey = T::class.java.name, entryValueQualifier = initializerQualifier)
    }
}

@ApplicationScope
@Module
private fun ComponentBuilder.esAppInitializerInjectionModule() {
    map<String, AppInitializer>(mapQualifier = AppInitializers)
    eager<AppInitRunner>()
}

@QualifierMarker
annotation class AppInitializers {
    companion object : Qualifier.Element
}

@Factory
private class AppInitRunner(
    private val logger: Logger,
    @AppInitializers private val initializers: Map<String, Provider<AppInitializer>>
) {
    init {
        initializers
            .forEach {
                logger.d(tag = "Init", message = "initialize ${it.key}")
                it.value()
            }
    }
}
