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

import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.BindingModule

@BindingModule(ApplicationComponent::class)
annotation class PermissionStateProviderBinding {
    @Module
    class ModuleImpl<T : PermissionStateProvider> {
        @SetElements
        operator fun invoke(instance: T): Set<PermissionStateProvider> = setOf(instance)
    }
}

@BindingModule(ApplicationComponent::class)
annotation class PermissionRequestHandlerBinding {
    @Module
    class ModuleImpl<T : PermissionRequestHandler> {
        @SetElements
        operator fun invoke(instance: T): Set<PermissionRequestHandler> =
            setOf(instance)
    }
}

@BindingModule(ApplicationComponent::class)
annotation class PermissionRequestRouteFactoryBinding {
    @Module
    class ModuleImpl<T : PermissionRequestRouteFactory> {
        @Binding
        val T.permissionRequestRouteFactory: PermissionRequestRouteFactory
            get() = this
    }
}
