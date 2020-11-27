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
import com.ivianuu.essentials.util.sortedGraph
import com.ivianuu.injekt.Arg
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.ForEffect
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.SetElements

@Effect
annotation class AppInitializerBinding(
    val key: String,
    val dependencies: Array<String> = [],
    val dependents: Array<String> = []
) {
    companion object {
        @SetElements
        fun <T : () -> Unit> appInitializerIntoSet(
            @Arg("key") key: String,
            @Arg("dependencies") dependencies: Array<String>?,
            @Arg("dependents") dependents: Array<String>?,
            content: @ForEffect T
        ): AppInitializers = setOf(AppInitializer(
            key = key,
            dependencies = dependencies?.toSet() ?: emptySet(),
            dependents = dependents?.toSet() ?: emptySet(),
            block = content
        ))
    }
}

data class AppInitializer(
    val key: String,
    val dependencies: Set<String>,
    val dependents: Set<String>,
    val block: () -> Unit
)

typealias AppInitializers = Set<AppInitializer>

@SetElements
fun defaultAppInitializers(): AppInitializers = setOf()

@FunBinding
fun runInitializers(
    initializers: AppInitializers,
    logger: Logger,
) {
    initializers
        .sortedGraph(
            key = { it.key },
            dependencies = { it.dependencies },
            dependents = { it.dependents }
        )
        .forEach {
            logger.d("Initialize ${it.key}")
            it.block()
        }
}
