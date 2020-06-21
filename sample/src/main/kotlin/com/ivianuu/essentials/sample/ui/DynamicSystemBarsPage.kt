package com.ivianuu.essentials.sample.ui

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.onPositioned
import androidx.ui.core.tag
import androidx.ui.foundation.Box
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.savedinstancestate.rememberSavedInstanceState
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.systemBarOverlayStyle
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.util.isLight
import com.ivianuu.injekt.Transient

@Transient
class DynamicSystemBarsPage {

    @Composable
    operator fun invoke() {
        Stack {
            val scrollerPosition = ScrollerPosition()

            VerticalScroller(scrollerPosition = scrollerPosition) {
                val colors: List<Color> = rememberSavedInstanceState {
                    ColorPickerPalette.values()
                        .filter { it != ColorPickerPalette.Black && it != ColorPickerPalette.White }
                        .flatMap { it.colors }
                        .shuffled()
                }

                colors.forEach { color ->
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .height(300.dp)
                            .tag(color)
                            .systemBarOverlayStyle(color.isLight),
                        backgroundColor = color
                    )
                }
            }

            TopAppBar(
                modifier = Modifier
                    .onPositioned {
                        println("${it.size.width} ${it.size.height}")
                    },
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                title = { Text("Dynamic system bars") }
            )
        }
    }

}
