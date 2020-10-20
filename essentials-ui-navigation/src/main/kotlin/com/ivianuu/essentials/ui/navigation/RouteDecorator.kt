package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.BindingModule

@BindingModule(ApplicationComponent::class)
annotation class RouteDecoratorBinding {
    @Module
    class ModuleImpl<T : @Composable (Route, @Composable () -> Unit) -> Unit> {
        @SetElements
        operator fun <T : @Composable (Route, @Composable () -> Unit) -> Unit> invoke(instance: T): RouteDecorators = setOf(instance)
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
