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

package com.ivianuu.essentials.permission

import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Name

@Name
annotation class PermissionStateProviders {
    companion object
}

inline fun <reified T : PermissionStateProvider> Module.bindPermissionStateProvider(
    name: Any? = null
) {
    withBinding<T>(name) { bindPermissionStateProvider() }
}

fun <T : PermissionStateProvider> BindingContext<T>.bindPermissionStateProvider(): BindingContext<T> {
    intoSet<PermissionStateProvider>(setName = PermissionStateProviders)
    return this
}

@Name
annotation class PermissionRequestHandlers {
    companion object
}

inline fun <reified T : PermissionRequestHandler> Module.bindPermissionRequestHandler(
    name: Any? = null
) {
    withBinding<T>(name) { bindPermissionRequestHandler() }
}

fun <T : PermissionRequestHandler> BindingContext<T>.bindPermissionRequestHandler(): BindingContext<T> {
    intoSet<PermissionRequestHandler>(setName = PermissionRequestHandlers)
    return this
}

inline fun <reified T : PermissionRequestUi> Module.bindPermissionRequestUi(
    name: Any? = null
) {
    withBinding<T>(name) { bindPermissionRequestUi() }
}


fun <T : PermissionRequestUi> BindingContext<T>.bindPermissionRequestUi(): BindingContext<T> {
    bindType<PermissionRequestUi>(override = true)
    return this
}