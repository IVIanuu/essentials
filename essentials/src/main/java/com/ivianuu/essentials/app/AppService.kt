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
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.ScopeOwner

object AppServices : StringQualifier("AppServices")

/**
 * Will be started on app start up and lives as long as the app lives
 */
abstract class AppService : ScopeOwner {

    override val scope: Scope get() = _scope
    private val _scope = MutableScope()

    open fun start() {
    }

}

inline fun <reified T : AppService> Module.appService(
    qualifier: Qualifier? = null,
    override: Boolean = false,
    noinline definition: Definition<T>
): BindingContext<T> =
    single(qualifier, null, override, false, definition) bindIntoClassMap AppServices

inline fun <reified T : AppService> Module.bindAppService(qualifier: Qualifier? = null) {
    bindIntoClassMap<T>(AppServices, implementationQualifier = qualifier)
}

val appServiceInjectionModule = module {
    mapBinding(AppServices)
}

val esAppServicesModule = module {
}