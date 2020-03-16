package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.ui.core.DrawClipToBounds
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
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
        modifier = modifier + DrawClipToBounds,
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
                width = max(placeables.maxBy { it.width }!!.width ?: 0.ipx, constraints.minWidth)
                height = placeables.sumBy { it.height.value }.ipx
            }
            Axis.Horizontal -> {
                width = placeables.sumBy { it.width.value }.ipx
                height =
                    max(placeables.maxBy { it.height }!!.height ?: 0.ipx, constraints.minHeight)
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
