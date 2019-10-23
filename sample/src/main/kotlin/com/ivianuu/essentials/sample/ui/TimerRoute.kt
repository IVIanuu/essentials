package com.ivianuu.essentials.sample.ui

import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.layout.Center
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.coroutines.collect
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.horizontal
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

val timerRoute = composeControllerRoute(
    options = controllerRouteOptions().horizontal()
) {
    Scaffold(
        appBar = { EsTopAppBar(title = "Timer") },
        content = {
            Center {
                val value = +collect(flow<Int> {
                    var i = 0
                    while (true) {
                        ++i
                        emit(i)
                        delay(1000)
                    }
                })

                Text(
                    text = "Value: $value",
                    style = +themeTextStyle { h1 }
                )
            }
        }
    )
}