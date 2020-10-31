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
import com.ivianuu.injekt.BindingAdapter
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.SetElements

@BindingAdapter
annotation class RouteDecoratorBinding {
    companion object {
        @SetElements
        fun <T : @Composable (Route, @Composable () -> Unit) -> Unit> routeDecoratorIntoSet(instance: T): RouteDecorators = setOf(instance)
    }
}

typealias RouteDecorators = Set<@Composable (Route, @Composable () -> Unit) -> Unit>

@SetElements
fun defaultRouteDecorators(): RouteDecorators = emptySet()

@FunBinding
@Composable
fun DecorateRoute(
    decorators: RouteDecorators,
    @FunApi route: Route,
    @FunApi children: @Composable () -> Unit
) {
    decorators.fold(children) { acc, decorator -> { decorator(route, acc) } }()
}
