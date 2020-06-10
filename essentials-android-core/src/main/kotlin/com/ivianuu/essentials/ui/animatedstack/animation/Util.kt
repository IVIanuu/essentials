package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.unit.PxBounds
import com.ivianuu.essentials.ui.animatable.AnimatableElement

@Composable
fun AnimatableElement.capturedBounds(): PxBounds? {
    val capturedBoundsState = state<PxBounds?> { null }
    if (capturedBoundsState.value == null && bounds != null) {
        capturedBoundsState.value = bounds
    }
    return capturedBoundsState.value
}
