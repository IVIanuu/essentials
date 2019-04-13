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

import com.ivianuu.essentials.injection.bindIntoClassMap
import com.ivianuu.injekt.*
import com.ivianuu.injekt.multibinding.mapBinding

object AppInitializers : StringQualifier("AppInitializers")

/**
 * Initializes what ever on app start up
 */
interface AppInitializer {
    fun initialize()
}

inline fun <reified T : AppInitializer> Module.appInitializer(
    qualifier: Qualifier? = null,
    override: Boolean = false,
    noinline definition: Definition<T>
): BindingContext<T> {
    return factory(
        qualifier = qualifier,
        override =  override,
        definition = definition
    ) bindIntoClassMap AppInitializers
}

inline fun <reified T : AppInitializer> Module.bindAppInitializer(qualifier: Qualifier? = null) {
    bindIntoClassMap<T>(AppInitializers, implementationQualifier = qualifier)
}

val appInitializerInjectionModule = module {
    mapBinding(AppInitializers)
}

val esAppInitializersModule = module {
    bindAppInitializer<RxJavaAppInitializer>()
    bindAppInitializer<SavedStateAppInitializer>()
    bindAppInitializer<StateStoreAppInitializer>()
    bindAppInitializer<TimberAppInitializer>()
}