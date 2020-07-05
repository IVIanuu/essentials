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
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.set
import com.ivianuu.injekt.unscoped

@BindingEffect(ApplicationComponent::class)
annotation class BindPermissionStateProvider {
    companion object {
        @Module
        operator fun <T : PermissionStateProvider> invoke() {
            set<PermissionStateProvider> { add<T>() }
        }
    }
}

@BindingEffect(ApplicationComponent::class)
annotation class BindPermissionRequestHandler {
    companion object {
        @Module
        operator fun <T : PermissionRequestHandler> invoke() {
            unscoped<T>()
            set<PermissionRequestHandler> { add<T>() }
        }
    }
}

@BindingEffect(ApplicationComponent::class)
annotation class BindPermissionRequestRouteFactory {
    companion object {
        @Module
        operator fun <T : PermissionRequestRouteFactory> invoke() {
            alias<T, PermissionRequestRouteFactory>()
        }
    }
}

@Module
fun EsPermissionsModule() {
    installIn<ApplicationComponent>()
    set<PermissionRequestHandler>()
    set<PermissionStateProvider>()
}
