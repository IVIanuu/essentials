package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import com.ivianuu.essentials.ui.animatable.Animatable
import com.ivianuu.essentials.ui.animatable.bounds
import com.ivianuu.essentials.ui.common.rememberUntrackedState

@Composable
fun Animatable.rememberFirstNonNullBounds(): Rect? {
    var capturedBoundsState by rememberUntrackedState<Rect?> { null }
    if (capturedBoundsState == null && bounds != null) {
        capturedBoundsState = bounds
    }
    return capturedBoundsState
}
