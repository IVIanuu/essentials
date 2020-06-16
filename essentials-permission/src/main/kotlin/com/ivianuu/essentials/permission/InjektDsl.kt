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

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.alias
import com.ivianuu.injekt.composition.BindingEffect
import com.ivianuu.injekt.composition.BindingEffectFunction
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.set
import com.ivianuu.injekt.transient

@BindingEffect(ApplicationComponent::class)
annotation class BindPermissionStateProvider

@BindingEffectFunction(BindPermissionStateProvider::class)
@Module
fun <T : PermissionStateProvider> permissionStateProvider() {
    set<PermissionStateProvider> { add<T>() }
}

@BindingEffect(ApplicationComponent::class)
annotation class BindPermissionRequestHandler

@BindingEffectFunction(BindPermissionRequestHandler::class)
@Module
fun <T : PermissionRequestHandler> permissionRequestProvider() {
    transient<T>()
    set<PermissionRequestHandler> { add<T>() }
}

@BindingEffect(ApplicationComponent::class)
annotation class BindPermissionRequestRouteFactory

@BindingEffectFunction(BindPermissionRequestRouteFactory::class)
@Module
fun <T : PermissionRequestRouteFactory> permissionRequestRouteFactory() {
    alias<T, PermissionRequestRouteFactory>()
}

@Module
fun esPermissionsModule() {
    installIn<ApplicationComponent>()
    set<PermissionRequestHandler>()
    set<PermissionStateProvider>()
}
