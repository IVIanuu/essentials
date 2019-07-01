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

import com.ivianuu.essentials.ui.twilight.TwilightController
import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Definition
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Name
import com.ivianuu.injekt.intoMap
import com.ivianuu.injekt.map
import com.ivianuu.injekt.module
import com.ivianuu.injekt.single
import com.ivianuu.injekt.withBinding
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.ScopeOwner
import kotlin.reflect.KClass

/**
 * Will be started on app start up and lives as long as the app lives
 */
abstract class AppService : ScopeOwner {

    private val _scope = MutableScope()
    override val scope: Scope get() = _scope

    open fun start() {
    }

}

inline fun <reified T : AppService> Module.appService(
    name: Any? = null,
    noinline definition: Definition<T>
): BindingContext<T> = single(name = name, definition = definition).bindAppService()

inline fun <reified T : AppService> Module.bindAppService(
    name: Any? = null
) {
    withBinding<T>(name) { bindAppService() }
}

inline fun <reified T : AppService> BindingContext<T>.bindAppService(): BindingContext<T> {
    intoMap<T, KClass<out AppService>, AppService>(
        entryKey = T::class, mapName = AppServices
    )
    return this
}

@Name(AppServices.Companion::class)
annotation class AppServices {
    companion object
}

val esAppServicesModule = module {
    map<KClass<out AppService>, AppService>(AppServices)
    bindAppService<TwilightController>()
}