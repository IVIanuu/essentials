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

import android.app.Application
import com.ivianuu.essentials.injection.bindIntoClassMap
import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Definition
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.module
import com.ivianuu.injekt.multibinding.mapBinding

const val APP_INITIALIZERS = "appInitializers"

/**
 * Initializes what ever on app start up
 */
interface AppInitializer {
    fun initialize(app: Application)
}

inline fun <reified T : AppInitializer> Module.appInitializer(
    name: String? = null,
    override: Boolean = false,
    noinline definition: Definition<T>
): BindingContext<T> =
    factory(name, null, override, definition) bindIntoClassMap APP_INITIALIZERS

inline fun <reified T : AppInitializer> Module.bindAppInitializer(name: String? = null) {
    bindIntoClassMap<T>(APP_INITIALIZERS, implementationName = name)
}

val esAppInitializersModule = module("EsAppInitializersModule") {
    mapBinding(APP_INITIALIZERS)
    bindAppInitializer<RxJavaAppInitializer>()
    bindAppInitializer<StateStoreAppInitializer>()
    bindAppInitializer<TimberAppInitializer>()
}