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
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given

@Effect
annotation class BindPermissionStateProvider {
    companion object {
        @SetElements(ApplicationComponent::class)
        @Reader
        operator fun <T : PermissionStateProvider> invoke(): Set<PermissionStateProvider> =
            setOf(given<T>())
    }
}

@Effect
annotation class BindPermissionRequestHandler {
    companion object {
        @SetElements(ApplicationComponent::class)
        @Reader
        operator fun <T : PermissionRequestHandler> invoke(): Set<PermissionRequestHandler> =
            setOf(given<T>())
    }
}

@Effect
annotation class BindPermissionRequestRouteFactory {
    companion object {
        @Given
        @Reader
        operator fun <T : PermissionRequestRouteFactory> invoke(): PermissionRequestRouteFactory =
            given<T>()
    }
}
