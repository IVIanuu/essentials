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

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.sortedGraph
import com.ivianuu.injekt.Arg
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.ForEffect
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.SetElements

@Effect
annotation class RouteDecoratorBinding(
    val key: String,
    val dependencies: Array<String> = [],
    val dependents: Array<String> = []
) {
    companion object {
        @SetElements
        fun <T : @Composable (Route, @Composable () -> Unit) -> Unit> routeDecoratorIntoSet(
            @Arg("key") key: String,
            @Arg("dependencies") dependencies: Array<String>?,
            @Arg("dependents") dependents: Array<String>?,
            content: @ForEffect T
        ): RouteDecorators = setOf(RouteDecorator(
            key = key,
            dependencies = dependencies?.toSet() ?: emptySet(),
            dependents = dependents?.toSet() ?: emptySet(),
            content = content as @Composable (Route, @Composable () -> Unit) -> Unit
        ))
    }
}

data class RouteDecorator(
    val key: String,
    val dependencies: Set<String>,
    val dependents: Set<String>,
    val content: @Composable (Route, @Composable () -> Unit) -> Unit
)

typealias RouteDecorators = Set<RouteDecorator>

@SetElements
fun defaultRouteDecorators(): RouteDecorators = emptySet()

@FunBinding
@Composable
fun DecorateRoute(
    decorators: RouteDecorators,
    logger: Logger,
    @FunApi route: Route,
    @FunApi children: @Composable () -> Unit
) {
    decorators
        .sortedGraph(
            key = { it.key },
            dependencies = { it.dependencies },
            dependents = { it.dependents }
        )
        .reversed()
        .fold(children) { acc, decorator ->
            {
                logger.d("Decorate route $route ${decorator.key}")
                decorator.content(route, acc)
            }
        }()
}
