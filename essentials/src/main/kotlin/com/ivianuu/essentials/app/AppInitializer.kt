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

import com.ivianuu.essentials.util.BoxLoggerAppInitializer
import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.ModuleBuilder
import com.ivianuu.injekt.Name

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
 * val analyticsModule = module {
 *     appInitializer { AnalyticsAppInitializer() }
 * }
 * ´´´
 *
 * Or for existing bindings
 *
 * ´´´
 * val analyticsModule = module {
 *     bindAppInitializer<AnalyticsInitializer>()
 * }
 * ´´´
 *
 */
interface AppInitializer

@Name
annotation class AppInitializers {
    companion object
}

inline fun <reified T : AppInitializer> ModuleBuilder.bindAppInitializer(
    name: Any? = null
) {
    withBinding<T>(name) { bindAppInitializer() }
}

inline fun <reified T : AppInitializer> BindingContext<T>.bindAppInitializer(): BindingContext<T> {
    intoMap<String, AppInitializer>(
        entryKey = T::class.java.name,
        mapName = AppInitializers
    )
    return this
}

val EsAppInitializersModule = Module {
    map<String, AppInitializer>(mapName = AppInitializers)
    bindAppInitializer<TimberAppInitializer>()
    bindAppInitializer<BoxLoggerAppInitializer>()
}
