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
import com.ivianuu.injekt.ModuleBuilder
import com.ivianuu.injekt.Name
import com.ivianuu.injekt.OverrideStrategy

@Name
annotation class PermissionStateProvidersSet {
    companion object
}

inline fun <reified T : PermissionStateProvider> ModuleBuilder.bindPermissionStateProvider(
    name: Any? = null
) {
    withBinding<T>(name = name) { bindPermissionStateProvider() }
}

fun <T : PermissionStateProvider> BindingContext<T>.bindPermissionStateProvider(): BindingContext<T> {
    intoSet<PermissionStateProvider>(setName = PermissionStateProvidersSet)
    return this
}

@Name
annotation class PermissionRequestHandlersSet {
    companion object
}

inline fun <reified T : PermissionRequestHandler> ModuleBuilder.bindPermissionRequestHandler(
    name: Any? = null
) {
    withBinding<T>(name = name) { bindPermissionRequestHandler() }
}

fun <T : PermissionRequestHandler> BindingContext<T>.bindPermissionRequestHandler(): BindingContext<T> {
    intoSet<PermissionRequestHandler>(setName = PermissionRequestHandlersSet)
    return this
}

inline fun <reified T : PermissionRequestUi> ModuleBuilder.bindPermissionRequestUi(
    name: Any? = null
) {
    withBinding<T>(name = name) { bindPermissionRequestUi() }
}

fun <T : PermissionRequestUi> BindingContext<T>.bindPermissionRequestUi(): BindingContext<T> {
    bindAlias<PermissionRequestUi>(overrideStrategy = OverrideStrategy.Override)
    return this
}
