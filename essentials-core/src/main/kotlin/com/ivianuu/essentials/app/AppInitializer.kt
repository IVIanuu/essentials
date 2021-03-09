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
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.sortedTopological
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.common.ForTypeKey
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.common.typeKeyOf

typealias AppInitializer = () -> Unit

@Qualifier
annotation class AppInitializerBinding

@Macro
@GivenSetElement
fun <T : @AppInitializerBinding S, @ForTypeKey S : AppInitializer> appInitializerBindingImpl(
    @Given instance: () -> T,
    @Given config: AppInitializerConfig<T> = AppInitializerConfig.DEFAULT
): AppInitializerElement = AppInitializerElement(
    typeKeyOf<S>(), instance, config
)

data class AppInitializerConfig<out T : AppInitializer>(
    val dependencies: Set<TypeKey<() -> Unit>> = emptySet(),
    val dependents: Set<TypeKey<() -> Unit>> = emptySet(),
) {
    companion object {
        val DEFAULT = AppInitializerConfig<Nothing>(emptySet(), emptySet())
    }
}

data class AppInitializerElement(
    val key: TypeKey<*>,
    val instance: () -> AppInitializer,
    val config: AppInitializerConfig<*>
)

typealias AppInitializerRunner = () -> Unit

@Given
fun appInitializerRunner(
    @Given initializers: Set<AppInitializerElement>,
    @Given logger: Logger,
): AppInitializerRunner = {
    initializers
        .sortedTopological(
            key = { it.key },
            dependencies = { it.config.dependencies },
            dependents = { it.config.dependents }
        )
        .forEach {
            logger.d { "Initialize ${it.key}" }
            it.instance()()
        }
}
