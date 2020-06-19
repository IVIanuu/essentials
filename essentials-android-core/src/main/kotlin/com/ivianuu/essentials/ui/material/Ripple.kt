package com.ivianuu.essentials.ui.material

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.StructurallyEqual
import androidx.compose.getValue
import androidx.compose.key
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.animation.asDisposableClock
import androidx.ui.core.AnimationClockAmbient
import androidx.ui.foundation.Interaction
import androidx.ui.graphics.Color
import androidx.ui.material.ripple.RippleIndication
import androidx.ui.material.ripple.RippleOpacity
import androidx.ui.material.ripple.RippleTheme
import androidx.ui.material.ripple.RippleThemeAmbient
import androidx.ui.unit.Dp

@Composable
fun RippleIndicationWithColorOpacity(
    bounded: Boolean = true,
    radius: Dp? = null,
    color: Color
): RippleIndication {
    val clock = AnimationClockAmbient.current.asDisposableClock()
    val colorState = state(StructurallyEqual) { color }
    colorState.value = color
    val opacity = remember(color) {
        object : RippleOpacity {
            override fun opacityForInteraction(interaction: Interaction): Float = color.alpha
        }
    }

    var rippleIndication: RippleIndication? by state { null }

    val currentRippleTheme = RippleThemeAmbient.current
    key(bounded, radius, clock) {
        Providers(
            RippleThemeAmbient provides remember(color) {
                object : RippleTheme by currentRippleTheme {

                    @Composable
                    override fun defaultColor(): Color = color

                    @Composable
                    override fun rippleOpacity(): RippleOpacity = opacity
                }
            }
        ) {
            rippleIndication = RippleIndication(
                bounded = bounded,
                radius = radius,
                color = color
            )
        }
    }

    return rippleIndication!!
}
