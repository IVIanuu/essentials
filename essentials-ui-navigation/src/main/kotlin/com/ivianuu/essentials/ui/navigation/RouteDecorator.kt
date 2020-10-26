package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.BindingAdapter
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
    route: @Assisted Route,
    children: @Assisted @Composable () -> Unit
) {
    decorators.fold(children) { acc, decorator -> { decorator(route, acc) } }()
}
