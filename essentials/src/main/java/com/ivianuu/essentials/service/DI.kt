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

package com.ivianuu.essentials.service

import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Name
import com.ivianuu.injekt.inject
import com.ivianuu.injekt.instance
import com.ivianuu.injekt.intoSet
import com.ivianuu.injekt.module
import com.ivianuu.injekt.parametersOf
import com.ivianuu.injekt.typeOf
import com.ivianuu.injekt.withBinding

@Name(AppServices.Companion::class)
annotation class AppServices {
    companion object
}

@Name(ActivityServices.Companion::class)
annotation class ActivityServices {
    companion object
}

@Name(ControllerServices.Companion::class)
annotation class ControllerServices {
    companion object
}

inline fun <reified T : Service> Module.bindAppService(
    bindingName: Any? = null,
    override: Boolean = false
) {
    withBinding<T>(bindingName) { bindAppService(override) }
}

inline fun <reified T : Service> Module.bindActivityService(
    bindingName: Any? = null,
    override: Boolean = false
) {
    withBinding<T>(bindingName) { bindActivityService(override) }
}

inline fun <reified T : Service> Module.bindControllerService(
    bindingName: Any? = null,
    override: Boolean = false
) {
    withBinding<T>(bindingName) { bindControllerService(override) }
}

inline fun <reified T : Service> Module.bindService(
    servicesName: Any,
    bindingName: Any? = null,
    override: Boolean = false
) {
    withBinding<T>(bindingName) { bindService(servicesName, override) }
}

inline fun <reified T : Service> BindingContext<T>.bindAppService(override: Boolean = false) {
    bindService(AppServices, override)
}

inline fun <reified T : Service> BindingContext<T>.bindActivityService(override: Boolean = false) {
    bindService(ActivityServices, override)
}

inline fun <reified T : Service> BindingContext<T>.bindControllerService(override: Boolean = false) {
    bindService(ControllerServices, override)
}

inline fun <reified T : Service> BindingContext<T>.bindService(
    name: Any,
    override: Boolean = false
) {
    intoSet<T, Service>(name, override)
}

fun serviceHostModule(host: ServiceHost) = module {
    instance(host, typeOf(ServiceHost::class), override = true)
}

fun InjektTrait.services(name: Any) = inject<ServiceManager> { parametersOf(name) }