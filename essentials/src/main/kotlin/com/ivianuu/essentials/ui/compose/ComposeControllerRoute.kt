package com.ivianuu.essentials.ui.compose

import androidx.compose.Composable
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRoute

fun composeControllerRoute(
    options: ControllerRoute.Options? = null,
    compose: @Composable() () -> Unit
) = controllerRoute(
    options = options,
    factory = {
        object : ComposeController() {
            override fun compose() {
                compose.invoke()
            }
        }
    }
)
