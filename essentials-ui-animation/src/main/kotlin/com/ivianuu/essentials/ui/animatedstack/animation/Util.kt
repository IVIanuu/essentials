package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import com.ivianuu.essentials.ui.animatable.Animatable
import com.ivianuu.essentials.ui.animatable.bounds
import com.ivianuu.essentials.ui.common.rememberUntrackedState

@Composable
fun Animatable.rememberFirstNonNullBounds(): Rect? {
    val capturedBoundsState = rememberUntrackedState<Rect?> { null }
    if (capturedBoundsState.value == null && bounds != null) {
        capturedBoundsState.value = bounds
    }
    return capturedBoundsState.value
}
