/*
 * Copyright 2020 Manuel Wrage
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
import com.ivianuu.injekt.BindingAdapter
import com.ivianuu.injekt.SetElements

@BindingAdapter
annotation class PermissionStateProviderBinding {
    companion object {
        @SetElements
        fun <T : PermissionStateProvider> stateProviderIntoSet(instance: T): Set<PermissionStateProvider> = setOf(instance)
    }
}

@BindingAdapter
annotation class PermissionRequestHandlerBinding {
    companion object {
        @SetElements
        fun <T : PermissionRequestHandler> requestHandlerIntoSet(instance: T): Set<PermissionRequestHandler> =
            setOf(instance)
    }
}

@BindingAdapter
annotation class PermissionRequestRouteFactoryBinding {
    companion object {
        @Binding
        inline val <T : PermissionRequestRouteFactory> T.permissionRequestRouteFactory: PermissionRequestRouteFactory
            get() = this
    }
}
