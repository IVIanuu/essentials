/*
 * Copyright 2018 Manuel Wrage
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
import com.ivianuu.injekt.bridge.bridge
import kotlin.reflect.KClass

/**
 * Initializes what ever on app start up
 */
interface AppInitializer {
    fun initialize()
}

inline fun <reified T : AppInitializer> Module.appInitializer(
    name: Qualifier? = null,
    noinline definition: Definition<T>
): Binding<T> = factory(name = name, definition = definition).bindAppInitializer()

inline fun <reified T : AppInitializer> Module.bindAppInitializer(
    name: Qualifier? = null
) {
    bridge<T>(name).bindAppInitializer()
}

@Name(AppInitializers.Companion::class)
annotation class AppInitializers {
    companion object : Qualifier
}

inline fun <reified T : AppInitializer> Binding<T>.bindAppInitializer() =
    bindIntoMap<T, KClass<out AppInitializer>, AppInitializer>(
        key = T::class, mapName = AppInitializers
    )

val esAppInitializersModule = module {
    bindAppInitializer<RxJavaAppInitializer>()
    bindAppInitializer<TimberAppInitializer>()
}