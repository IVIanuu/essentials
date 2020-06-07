package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.ui.core.AnimationClockAmbient
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.animation.FlingConfig
import com.ivianuu.essentials.ui.core.rememberRetained

@Composable
fun RetainedScrollerPosition(
    initial: Float = 0f,
    isReversed: Boolean = false
): ScrollerPosition {
    val clock = AnimationClockAmbient.current
    val config = FlingConfig()
    return rememberRetained(clock, config) {
        ScrollerPosition(
            flingConfig = config,
            initial = initial,
            animationClock = clock,
            isReversed = isReversed
        )
    }
}
