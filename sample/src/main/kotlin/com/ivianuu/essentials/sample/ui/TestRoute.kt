package com.ivianuu.essentials.sample.ui

import androidx.compose.remember
import androidx.ui.foundation.Clickable
import androidx.ui.layout.Container
import androidx.ui.material.ripple.Ripple
import androidx.ui.unit.dp
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.common.SimpleScreen
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.layout.ScrollableList2
import com.ivianuu.essentials.ui.material.Surface
import com.ivianuu.essentials.ui.navigation.DefaultRouteTransition
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.util.Toaster

val TestRoute = Route(transition = DefaultRouteTransition) {
    SimpleScreen(title = "Test") {
        ScrollableList2(items = (1..10000).toList()) { _, item ->
            d { "invoke item $item" }
            Ripple(bounded = true) {
                val toaster = inject<Toaster>()
                Clickable(onClick = { toaster.toast("Clicked $item") }) {
                    val color = remember {
                        ColorPickerPalette.values()
                            .filter { it != ColorPickerPalette.Black && it != ColorPickerPalette.White && it != ColorPickerPalette.Grey }
                            .flatMap { it.colors }
                            .shuffled()
                            .first()
                    }
                    Surface(color = color) {
                        Container(height = 88.dp) {
                        /*    val timerValue = collect(remember { timerFlow() })
                            d { "invoke item with timer $item $timerValue" }
                            Text(text = "Item $item timer $timerValue")*/
                        }
                    }
                }
            }
        }
    }
}
