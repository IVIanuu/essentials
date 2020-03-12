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

import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.QualifierMarker
import com.ivianuu.injekt.common.map

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

fun ComponentBuilder.esAppInitializerInjection() {
    map<String, AppInitializer>(mapQualifier = AppInitializers)
}

@QualifierMarker
annotation class AppInitializers {
    companion object : Qualifier.Element
}
