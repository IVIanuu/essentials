package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.ui.core.AnimationClockAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.gestures.DragDirection
import com.ivianuu.essentials.ui.core.retain
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.Row

@Composable
fun Scroller(
    modifier: Modifier = Modifier.None,
    scrollerPosition: ScrollerPosition = RetainedScrollerPosition(),
    dragDirection: DragDirection = DragDirection.Vertical,
    scrollable: Boolean = false,
    children: @Composable () -> Unit
) {
    when (dragDirection) {
        DragDirection.Horizontal -> {
            HorizontalScroller(
                modifier = modifier,
                scrollerPosition = scrollerPosition,
                isScrollable = scrollable
            ) {
                Row {
                    children()
                }
            }
        }
        DragDirection.Vertical -> {
            VerticalScroller(
                modifier = modifier,
                scrollerPosition = scrollerPosition,
                isScrollable = scrollable
            ) {
                Column {
                    children()
                }
            }
        }
    }
}

@Composable
fun RetainedScrollerPosition(
    initial: Float = 0f
): ScrollerPosition {
    val clock = AnimationClockAmbient.current
    val config = FlingConfig()
    return retain(clock, config) {
        ScrollerPosition(flingConfig = config, initial = initial, animationClock = clock)
    }
}
