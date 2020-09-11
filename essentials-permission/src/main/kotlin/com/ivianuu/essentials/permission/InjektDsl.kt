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

import com.ivianuu.injekt.ContextBuilder
import com.ivianuu.injekt.Key
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.common.Adapter

@Adapter
annotation class GivenPermissionStateProvider {
    companion object : Adapter.Impl<PermissionStateProvider> {
        override fun ContextBuilder.configure(
            key: Key<PermissionStateProvider>,
            provider: @Reader () -> PermissionStateProvider
        ) {
            set<PermissionStateProvider> {
                add(key, elementProvider = provider)
            }
        }
    }
}

@Adapter
annotation class GivenPermissionRequestHandler {
    companion object : Adapter.Impl<PermissionRequestHandler> {
        override fun ContextBuilder.configure(
            key: Key<PermissionRequestHandler>,
            provider: @Reader () -> PermissionRequestHandler
        ) {
            set<PermissionRequestHandler> {
                add(key, elementProvider = provider)
            }
        }
    }
}

@Adapter
annotation class GivenPermissionRequestRouteFactory {
    companion object : Adapter.Impl<PermissionRequestRouteFactory> {
        override fun ContextBuilder.configure(
            key: Key<PermissionRequestRouteFactory>,
            provider: @Reader () -> PermissionRequestRouteFactory
        ) {
            unscoped<PermissionRequestRouteFactory>(provider = provider)
        }
    }
}
