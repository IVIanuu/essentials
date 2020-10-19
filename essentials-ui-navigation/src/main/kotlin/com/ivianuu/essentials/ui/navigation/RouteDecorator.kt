package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.BindingModule

@BindingModule(ApplicationComponent::class)
annotation class RouteDecoratorBinding {
    @Module
    class ModuleImpl<T : RouteDecorator> {
        @SetElements
        operator fun <T : RouteDecorator> invoke(instance: T): RouteDecorators = setOf(instance)
    }
}

typealias RouteDecorator = @Composable (Route, @Composable () -> Unit) -> Unit

typealias RouteDecorators = Set<RouteDecorator>

@SetElements
fun defaultRouteDecorators(): RouteDecorators = emptySet()

typealias DecorateRoute = @Composable (Route, @Composable () -> Unit) -> Unit
@Binding
fun DecorateRoute(decorators: RouteDecorators): DecorateRoute = { route, children ->
    decorators.fold(children) { acc, decorator -> { decorator(route, acc) } }()
}
