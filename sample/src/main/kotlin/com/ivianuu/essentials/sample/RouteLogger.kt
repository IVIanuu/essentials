package com.ivianuu.essentials.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.RouteDecorator
import com.ivianuu.essentials.ui.navigation.RouteDecoratorBinding
import com.ivianuu.essentials.util.Logger

@RouteDecoratorBinding
fun RouteLogger(logger: Logger): RouteDecorator = { route, children ->
    onActive {
        logger.d("hello from route $route")
        onDispose {
            logger.d("bye from route $route")
        }
    }
    children()
}
