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

fun <T : () -> Unit> appInitializerBinding(
    key: String,
    dependencies: Set<String> = emptySet(),
    dependents: Set<String> = emptySet()
): @GivenSetElement (@Given T) -> AppInitializer = { content ->
    AppInitializer(
        key = key,
        dependencies = dependencies,
        dependents = dependents,
        block = content
    )
}

data class AppInitializer(
    val key: String,
    val dependencies: Set<String>,
    val dependents: Set<String>,
    val block: () -> Unit
)

@GivenFun
fun runInitializers(
    @Given initializers: Set<AppInitializer>,
    @Given logger: Logger,
) {
    initializers
        .sortedGraph(
            key = { it.key },
            dependencies = { it.dependencies },
            dependents = { it.dependents }
        )
        .forEach {
            logger.d { "Initialize ${it.key}" }
            it.block()
        }
}
