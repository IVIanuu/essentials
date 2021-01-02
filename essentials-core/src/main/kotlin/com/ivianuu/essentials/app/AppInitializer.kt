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
import com.ivianuu.essentials.util.sortedGraph
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.common.ForKey
import com.ivianuu.injekt.common.Key
import com.ivianuu.injekt.common.keyOf

@Qualifier annotation class AppInitializerBinding
@Macro @GivenSetElement
fun <@ForKey T : @AppInitializerBinding () -> Unit> appInitializerBindingImpl(
    @Given instance: T,
    @Given config: AppInitializerConfig<T> = AppInitializerConfig.DEFAULT
): AppInitializerElement = AppInitializerElement(
    keyOf<T>(), instance, config
)

@Qualifier annotation class AppInitializerConfigBinding<T : () -> Unit>

data class AppInitializerConfig<out T : () -> Unit>(
    val dependencies: Set<Key<() -> Unit>> = emptySet(),
    val dependents: Set<Key<() -> Unit>> = emptySet(),
) {
    companion object {
        val DEFAULT = AppInitializerConfig<Nothing>(emptySet(), emptySet())
    }
}

data class AppInitializerElement(
    val key: Key<*>,
    val instance: () -> Unit,
    val config: AppInitializerConfig<*>
)

@GivenFun
fun runInitializers(
    @Given initializers: Set<AppInitializerElement>,
    @Given logger: Logger,
) {
    initializers
        .sortedGraph(
            key = { it.key },
            dependencies = { it.config.dependencies },
            dependents = { it.config.dependents }
        )
        .forEach {
            logger.d { "Initialize ${it.key}" }
            it.instance()
        }
}
