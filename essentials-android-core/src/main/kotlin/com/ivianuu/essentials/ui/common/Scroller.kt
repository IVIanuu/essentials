package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.clipToBounds
import androidx.ui.unit.IntPx
import androidx.ui.unit.Px
import androidx.ui.unit.ipx
import androidx.ui.unit.max
import androidx.ui.unit.px
import androidx.ui.unit.round
import com.ivianuu.essentials.ui.core.Axis

@Composable
fun Scroller(
    modifier: Modifier = Modifier.None,
    direction: Axis = Axis.Vertical,
    scrollableState: ScrollableState = RetainedScrollableState(),
    enabled: Boolean = true,
    children: @Composable () -> Unit
) {
    Scrollable(
        state = scrollableState,
        direction = direction,
        enabled = enabled
    ) {
        ScrollerLayout(
            scrollableState,
            modifier = modifier,
            direction = direction,
            children = children
        )
    }
}

@Composable
private fun ScrollerLayout(
    scrollableState: ScrollableState,
    modifier: Modifier,
    direction: Axis,
    children: @Composable () -> Unit
) {
    Layout(
        modifier = modifier.clipToBounds(),
        children = children
    ) { measurables, constraints, _ ->
        if (measurables.isEmpty()) return@Layout layout(
            constraints.minWidth,
            constraints.minHeight
        ) {}

        val childConstraints = constraints.copy(
            maxHeight = when (direction) {
                Axis.Vertical -> IntPx.Infinity
                Axis.Horizontal -> constraints.maxHeight
            },
            maxWidth = when (direction) {
                Axis.Vertical -> constraints.maxWidth
                Axis.Horizontal -> IntPx.Infinity
            }
        )

        val placeables = measurables.map { it.measure(childConstraints) }

        val width: IntPx
        val height: IntPx
        when (direction) {
            Axis.Vertical -> {
                width = constraints.maxWidth
                height = placeables.sumBy { it.height.value }.ipx
                    .coerceIn(constraints.minHeight, constraints.maxHeight)
            }
            Axis.Horizontal -> {
                width = placeables.sumBy { it.width.value }.ipx
                    .coerceIn(constraints.minWidth, constraints.maxWidth)
                height = constraints.maxHeight
            }
        }

        layout(width, height) {
            val newMaxValue = when (direction) {
                Axis.Vertical -> max(
                    0.px,
                    placeables.sumBy { it.height.value }.px - height
                )
                Axis.Horizontal -> max(
                    Px.Zero,
                    placeables.sumBy { it.width.value }.px - width
                )
            }

            if (scrollableState.maxValue != newMaxValue) {
                scrollableState.updateBounds(Px.Zero, newMaxValue)
            }

            var offset = -scrollableState.value.round()
            placeables.forEach { placeable ->
                @Suppress("LiftReturnOrAssignment")
                when (direction) {
                    Axis.Vertical -> {
                        placeable.place(0.ipx, offset)
                        offset += placeable.height
                    }
                    Axis.Horizontal -> {
                        placeable.place(offset, 0.ipx)
                        offset += placeable.width
                    }
                }
            }
        }
    }
}
