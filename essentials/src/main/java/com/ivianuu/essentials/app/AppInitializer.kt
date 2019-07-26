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

import com.ivianuu.injekt.*
import kotlin.reflect.KClass

/**
 * Will be instantiated on app start up
 * Can be used to initialize global stuff like logging
 *
 * class AnalyticsInitializer {
 *     init {
 *         Analytics.initialize(Logger())
 *     }
 * }
 *
 *
 * Must be bound inside your module like this:
 *
 * val analyticsModule = module {
 *     appInitializer { AnalyticsAppInitializer() }
 * }
 *
 * or for existing bindings
 *
 * val analyticsModule = module {
 *     bindAppInitializer<AnalyticsInitializer>()
 * }
 *
 */
interface AppInitializer


inline fun <reified T : AppInitializer> Module.appInitializer(
    name: Any? = null,
    noinline definition: Definition<T>
): BindingContext<T> = factory(name = name, definition = definition).bindAppInitializer()

inline fun <reified T : AppInitializer> Module.bindAppInitializer(
    name: Any? = null
) {
    withBinding<T>(name) { bindAppInitializer() }
}

@Name(AppInitializers.Companion::class)
annotation class AppInitializers {
    companion object
}

inline fun <reified T : AppInitializer> BindingContext<T>.bindAppInitializer(): BindingContext<T> {
    intoMap<T, KClass<out AppInitializer>, AppInitializer>(
        entryKey = T::class,
        mapName = AppInitializers
    )
    return this
}

val esAppInitializersModule = module {
    map<KClass<out AppInitializer>, AppInitializer>(AppInitializers)
    bindAppInitializer<RxJavaAppInitializer>()
    bindAppInitializer<TimberAppInitializer>()
}