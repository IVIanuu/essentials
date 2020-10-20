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

package com.ivianuu.essentials.app

import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.BindingModule

@BindingModule(ApplicationComponent::class)
annotation class AppInitializerBinding {
    @Module
    class ModuleImpl<T : AppInitializer> {
        @SetElements
        fun intoSet(instance: T): AppInitializers = setOf(instance)
    }
}

typealias AppInitializer = () -> Unit
typealias AppInitializers = Set<AppInitializer>

@SetElements
fun defaultAppInitializers(): AppInitializers = emptySet()

typealias runInitializers = () -> Unit
@FunBinding
fun runInitializers(
    appInitializers: AppInitializers,
    logger: Logger,
) {
    logger.d("run initializers")
    appInitializers
        .forEach { it() }
}
