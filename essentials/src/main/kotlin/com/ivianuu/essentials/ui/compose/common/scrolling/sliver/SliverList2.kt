/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.compose.common.scrolling.sliver

import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Constraints
import androidx.ui.core.Direction
import androidx.ui.core.ParentData
import androidx.ui.core.Placeable
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.coerceIn
import androidx.ui.core.px
import androidx.ui.core.round
import androidx.ui.core.toPx
import com.github.ajalt.timberkt.d

// todo add SliverFixedSizeList

// todo rename item arg

fun SliverChildren.SliverList2(
    count: Int,
    item: @Composable() (Int) -> Unit
) {
    Sliver(children = {
        (0 until count).forEach { index ->
            ParentData(+memo { ListParentData(index) }) {
                item(index)
            }
        }
    }) { measureables, constraints ->
        if (measureables.isEmpty()) return@Sliver layout(SliverGeometry.Zero) {}

        val placeables = mutableListOf<ListPlaceable>()

        val scrollPosition = constraints.scrollPosition// + constraints.cacheOrigin
        val remainingSpace = constraints.remainingPaintSpace //.remainingCacheSpace
        val targetEndScrollPosition = scrollPosition + remainingSpace
        val childConstraints = constraints.asConstraints()
        var reachedEnd = false

        fun firstChild(): ListPlaceable? = placeables.firstOrNull()
        fun lastChild(): ListPlaceable? = placeables.lastOrNull()

        fun getMeasuredChild(index: Int, constraints: Constraints): ListPlaceable? {
            placeables.getOrNull(index)?.let { return it }
            val child = measureables.getOrNull(index)
            if (child != null) {
                val placeable = child.measure(constraints)
                return ListPlaceable(placeable, child.parentData as ListParentData)
                    .also { placeables += it }
            }
            return null
        }

        var leadingChild: ListPlaceable? = null
        var trailingChild: ListPlaceable? = null
        var earliestUsefulChild: ListPlaceable? = getMeasuredChild(0, childConstraints)

        var earlistLayoutOffset = earliestUsefulChild!!.parentData.layoutOffset
        while (earlistLayoutOffset > scrollPosition) {
            d { "layout top earliest layout offset $earlistLayoutOffset scroll pos $scrollPosition" }
            earliestUsefulChild =
                getMeasuredChild(placeables.indexOf(firstChild()) - 1, childConstraints)
            if (earliestUsefulChild == null) {
                firstChild()!!.parentData.layoutOffset = Px.Zero
                if (scrollPosition == Px.Zero) {
                    earliestUsefulChild = firstChild()
                    leadingChild = earliestUsefulChild
                    if (trailingChild == null)
                        trailingChild = earliestUsefulChild
                    break
                } else {
                    d { "request correction ${-scrollPosition}" }
                    return@Sliver layout(SliverGeometry(scrollOffsetCorrection = -scrollPosition)) {}
                }
            }

            val firstChildLayoutOffset = earlistLayoutOffset - firstChild()!!.paintSize(constraints)

            val childParentData = earliestUsefulChild
            childParentData.parentData.layoutOffset = firstChildLayoutOffset
            leadingChild = earliestUsefulChild
            if (trailingChild == null)
                trailingChild = earliestUsefulChild

            earlistLayoutOffset = earliestUsefulChild.parentData.layoutOffset
        }

        earliestUsefulChild!!

        if (leadingChild == null) {
            leadingChild = earliestUsefulChild
            trailingChild = earliestUsefulChild
        }

        var inLayoutRange = true
        var child: ListPlaceable? = earliestUsefulChild
        var index = placeables.indexOf(child!!)
        var endScrollPosition = child.parentData.layoutOffset + child.paintSize(constraints)

        d { "child $child index $index end scroll position $endScrollPosition" }

        fun advance(): Boolean {
            if (child == trailingChild) inLayoutRange = false
            child = getMeasuredChild(placeables.indexOf(child!!) + 1, childConstraints)
            if (child == null) inLayoutRange = false
            index += 1
            if (!inLayoutRange) {
                if (child == null || placeables.indexOf(child!!) != index) {
                    child =
                        getMeasuredChild(placeables.indexOf(trailingChild!!) + 1, childConstraints)
                    if (child == null) {
                        d { "run out of children" }
                        return false
                    }
                }
                trailingChild = child
            }
            child!!.parentData.layoutOffset = endScrollPosition
            endScrollPosition += child!!.paintSize(constraints)
            return true
        }

        while (endScrollPosition < scrollPosition) {
            d { "end scroll position $endScrollPosition is lesser than scroll position $scrollPosition" }
            if (!advance()) {
                val size =
                    lastChild()!!.parentData.layoutOffset + lastChild()!!.paintSize(constraints)
                d { "cannot advance more size is $size" }
                val geometry = SliverGeometry(
                    scrollSize = size,
                    paintSize = Px.Zero,
                    maxPaintSize = size
                )

                return@Sliver layout(geometry) { place(placeables, geometry, constraints) }
            }
        }

        while (endScrollPosition < targetEndScrollPosition) {
            d { "end scroll position $endScrollPosition is lesser than target end scroll position $targetEndScrollPosition" }
            if (!advance()) {
                d { "reached end" }
                reachedEnd = true
                break
            }
        }

        val estimatedMaxScrollPosition = if (reachedEnd) {
            endScrollPosition
        } else {
            Px.Infinity // todo
        }

        d { "estimated max scroll position $estimatedMaxScrollPosition" }

        val paintSize = calculatePaintSize(
            constraints = constraints,
            from = firstChild()!!.parentData.layoutOffset,
            to = endScrollPosition
        )

        val cacheSize = calculateCacheSize(
            constraints,
            from = firstChild()!!.parentData.layoutOffset,
            to = endScrollPosition
        )

        val targetEndScrollOffsetForPaint =
            constraints.scrollPosition + constraints.remainingPaintSpace

        val geometry = SliverGeometry(
            scrollSize = estimatedMaxScrollPosition,
            paintSize = paintSize,
            cacheSize = cacheSize,
            maxPaintSize = estimatedMaxScrollPosition,
            hasVisualOverflow = endScrollPosition > targetEndScrollOffsetForPaint || constraints.scrollPosition > Px.Zero
        )

        layout(geometry) { place(placeables, geometry, constraints) }
    }
}

private data class ListPlaceable(
    val placeable: Placeable,
    val parentData: ListParentData
)

private data class ListParentData(
    val index: Int,
    var layoutOffset: Px = Px.Zero
)

private fun ListPlaceable.paintSize(constraints: SliverConstraints): Px =
    when (constraints.mainAxisDirection) {
        Direction.LEFT, Direction.RIGHT -> placeable.width.toPx()
        Direction.UP, Direction.DOWN -> placeable.height.toPx()
    }

private fun calculatePaintSize(
    constraints: SliverConstraints,
    from: Px,
    to: Px
): Px {
    val a = constraints.scrollPosition
    val b = constraints.scrollPosition + constraints.remainingPaintSpace
    return (to.coerceIn(a, b) - from.coerceIn(a, b)).coerceIn(
        Px.Zero,
        constraints.remainingPaintSpace
    )
}

private fun calculateCacheSize(
    constraints: SliverConstraints,
    from: Px,
    to: Px
): Px {
    val a = constraints.scrollPosition + constraints.cacheOrigin
    val b = constraints.scrollPosition + constraints.remainingCacheSpace
    return (to.coerceIn(a, b) - from.coerceIn(a, b)).coerceIn(
        Px.Zero,
        constraints.remainingCacheSpace
    )
}

private fun Placeable.PlacementScope.place(
    placeables: List<ListPlaceable>,
    geometry: SliverGeometry,
    constraints: SliverConstraints
) {
    val mainAxisUnit: PxPosition
    val crossAxisUnit: PxPosition
    val originOffset: PxPosition
    val addSize: Boolean

    when (constraints.mainAxisDirection.applyGrowthDirection(constraints.growthDirection)) {
        Direction.LEFT -> {
            mainAxisUnit = PxPosition((-1).px, Px.Zero)
            crossAxisUnit = PxPosition(Px.Zero, 1.px)
            originOffset = PxPosition(geometry.paintSize, Px.Zero)
            addSize = true
        }
        Direction.UP -> {
            mainAxisUnit = PxPosition(Px.Zero, (-1).px)
            crossAxisUnit = PxPosition(1.px, Px.Zero)
            originOffset = PxPosition(Px.Zero, geometry.paintSize)
            addSize = true
        }
        Direction.RIGHT -> {
            mainAxisUnit = PxPosition(1.px, Px.Zero)
            crossAxisUnit = PxPosition(Px.Zero, 1.px)
            originOffset = PxPosition.Origin
            addSize = false
        }
        Direction.DOWN -> {
            mainAxisUnit = PxPosition(Px.Zero, 1.px)
            crossAxisUnit = PxPosition(1.px, Px.Zero)
            originOffset = PxPosition.Origin
            addSize = false
        }
    }

    for (child in placeables) {
        val mainAxisDelta = child.parentData.layoutOffset - constraints.scrollPosition
        val crossAxisDelta = Px.Zero
        var childOffset = PxPosition(
            x = (originOffset.x.value + mainAxisUnit.x.value * mainAxisDelta.value + crossAxisUnit.x.value * crossAxisDelta.value).px,
            y = (originOffset.y.value + mainAxisUnit.y.value * mainAxisDelta.value + crossAxisUnit.y.value * crossAxisDelta.value).px
        )

        val paintSize = when (constraints.mainAxisDirection) {
            Direction.LEFT, Direction.RIGHT -> child.placeable.width
            Direction.UP, Direction.DOWN -> child.placeable.height
        }

        if (addSize) {
            childOffset = PxPosition(
                x = childOffset.x + (mainAxisUnit.x.value * paintSize.value).px,
                y = childOffset.y + (mainAxisUnit.y.value * paintSize.value).px
            )
        }

        if (mainAxisDelta < constraints.remainingPaintSpace && mainAxisDelta + paintSize > Px.Zero) {
            child.placeable.place(childOffset.x.round(), childOffset.y.round())
            d { "place child ${child.parentData.index} to x ${childOffset.x.round()} y ${childOffset.y.round()}" }
        }
    }
}