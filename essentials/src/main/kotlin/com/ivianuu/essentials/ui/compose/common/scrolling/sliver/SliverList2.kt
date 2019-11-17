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
import androidx.compose.CompositionReference
import androidx.compose.Observe
import androidx.compose.ambient
import androidx.compose.compositionReference
import androidx.compose.memo
import androidx.compose.onPreCommit
import androidx.compose.unaryPlus
import androidx.ui.core.Constraints
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Direction
import androidx.ui.core.Dp
import androidx.ui.core.LayoutNode
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.coerceIn
import androidx.ui.core.px
import androidx.ui.core.round
import androidx.ui.core.toPx
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.ChildManager
import com.ivianuu.essentials.ui.compose.core.ChildManagerParentData
import com.ivianuu.essentials.ui.compose.core.ref

// todo add SliverFixedSizeList

// todo rename item arg
fun SliverChildren.SliverList(
    count: Int,
    itemSizeProvider: (Int) -> Dp,
    item: @Composable() (Int) -> Unit
) {
    val context = +ambient(ContextAmbient)
    var reference: CompositionReference? = null
    val childManagerRef = +ref<ChildManager?> { null }

    Observe {
        d { "invoke observe" }
        reference = +compositionReference()
        childManagerRef.value?.recompose()
        +onPreCommit(true) {
            onDispose {
                childManagerRef.value!!.dispose()
            }
        }
    }

    childManagerRef.value = +memo {
        ChildManager(
            context = context,
            compositionReference = reference!!
        )
    }

    val childManager = childManagerRef.value!!

    Sliver(children = {}) { measureables, constraints ->
        childManagerRef.value!!.setLayoutNode(layoutNode)

        d { "sliver list layout $constraints" }
        var leadingGarbage = 0
        var trailingGarbage = 0
        val scrollPosition = constraints.scrollPosition// + constraints.cacheOrigin
        val remainingSpace = constraints.remainingPaintSpace //.remainingCacheSpace
        val targetEndScrollPosition = scrollPosition + remainingSpace
        val childConstraints = constraints.asConstraints()
        var reachedEnd = false

        fun createOrObtainChild(index: Int): LayoutNode? {
            d { "create or obtain child at $index" }
            if (index < 0) return null
            var child = childManager.get(index)
            if (child != null) return child
            if (index > count - 1) return null

            val composable = { item(index) }

            childManager.add(index, composable)
            child = childManager.get(index)!!
            child!!.data.index = index

            return child
        }

        fun firstChild(): LayoutNode? =
            layoutNode.layoutChildren.minBy { it.data.index }

        fun lastChild(): LayoutNode? =
            layoutNode.layoutChildren.maxBy { it.data.index }

        fun childBefore(child: LayoutNode) = layoutNode.layoutChildren.firstOrNull {
            it.data.index == child.data.index - 1
        }

        fun childAfter(child: LayoutNode) = layoutNode.layoutChildren.firstOrNull {
            it.data.index == child.data.index + 1
        }.also { d { "child after ${child.data.index} is ${it?.data?.index}" } }

        fun insertAndLayoutChild(after: LayoutNode, constraints: Constraints): LayoutNode? {
            val index = after.data.index + 1
            d { "insert and layout child $index" }
            val child = createOrObtainChild(index)
            if (child != null) {
                try {
                    d { "measure child $index" }
                    child.measure(constraints)
                } catch (e: Exception) {
                }
                return child
            }
            return null
        }

        fun insertAndLayoutLeadingChild(constraints: Constraints): LayoutNode? {
            val firstChild = firstChild()
            val index = if (firstChild != null) {
                firstChild.data.index - 1
            } else 0
            d { "insert and layout leading child $index" }

            val child = createOrObtainChild(index)
            if (child != null) {
                try {
                    d { "measure child $index" }
                    child.measure(constraints)
                } catch (e: Exception) {
                }
                return child
            }

            return null
        }

        fun LayoutNode.paintSize(): Px = when (constraints.mainAxisDirection) {
            Direction.LEFT, Direction.RIGHT -> width.toPx()
            Direction.UP, Direction.DOWN -> height.toPx()
        }

        fun place(geometry: SliverGeometry) {
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

            for (child in layoutNode.layoutChildren) {
                val parentData = child.data

                val mainAxisDelta = parentData.layoutOffset - constraints.scrollPosition
                val crossAxisDelta = Px.Zero
                var childOffset = PxPosition(
                    x = (originOffset.x.value + mainAxisUnit.x.value * mainAxisDelta.value + crossAxisUnit.x.value * crossAxisDelta.value).px,
                    y = (originOffset.y.value + mainAxisUnit.y.value * mainAxisDelta.value + crossAxisUnit.y.value * crossAxisDelta.value).px
                )

                val paintSize = when (constraints.mainAxisDirection) {
                    Direction.LEFT, Direction.RIGHT -> child.width
                    Direction.UP, Direction.DOWN -> child.height
                }

                if (addSize) {
                    childOffset = PxPosition(
                        x = childOffset.x + (mainAxisUnit.x.value * paintSize.value).px,
                        y = childOffset.y + (mainAxisUnit.y.value * paintSize.value).px
                    )
                }

                //d { "child at $index main axis delta $mainAxisDelta child offset $childOffset paint size $paintSize remaining ${constraints.remainingPaintSpace}" }

                if (mainAxisDelta < constraints.remainingPaintSpace && mainAxisDelta + paintSize > Px.Zero) {
                    child.place(childOffset.x.round(), childOffset.y.round())
                }
            }
        }

        fun collectGarbage(leadingGarbage: Int, trailingGarbage: Int) {
            var leadingGarbage = leadingGarbage
            var trailingGarbage = trailingGarbage
            while (leadingGarbage > 0) {
                childManager.remove(firstChild()!!.data.index)
                leadingGarbage -= 1
            }
            while (trailingGarbage > 0) {
                childManager.remove(lastChild()!!.data.index)
                trailingGarbage -= 1
            }
        }

        if (layoutNode.layoutChildren.isEmpty()) {
            if (insertAndLayoutLeadingChild(childConstraints) == null) {
                return@Sliver layout(SliverGeometry.Zero) {}
            }
        }

        var leadingChildWithLayout: LayoutNode? = null
        var trailingChildWithLayout: LayoutNode? = null
        var earliestUsefulChild = firstChild()
        var earlistLayoutOffset = earliestUsefulChild!!.data.layoutOffset
        d { "earliest useful child $earliestUsefulChild layout offset $earlistLayoutOffset" }
        while (earlistLayoutOffset > scrollPosition) {
            d { "layout top earliest layout offset $earlistLayoutOffset scroll pos $scrollPosition" }
            earliestUsefulChild = insertAndLayoutLeadingChild(childConstraints)
            if (earliestUsefulChild == null) {
                firstChild()!!.data.layoutOffset = Px.Zero
                if (scrollPosition == Px.Zero) {
                    earliestUsefulChild = firstChild()
                    leadingChildWithLayout = earliestUsefulChild
                    if (trailingChildWithLayout == null)
                        trailingChildWithLayout = earliestUsefulChild
                    break
                } else {
                    d { "request correction ${-scrollPosition}" }
                    return@Sliver layout(SliverGeometry(scrollOffsetCorrection = -scrollPosition)) {}
                }
            }

            val firstChildLayoutOffset = earlistLayoutOffset - firstChild()!!.paintSize()

            val childParentData = earliestUsefulChild.data
            childParentData.layoutOffset = firstChildLayoutOffset
            leadingChildWithLayout = earliestUsefulChild
            if (trailingChildWithLayout == null)
                trailingChildWithLayout = earliestUsefulChild

            earlistLayoutOffset = earliestUsefulChild.data.layoutOffset
        }

        earliestUsefulChild!!

        if (leadingChildWithLayout == null) {
            try {
                d { "measure child ${earliestUsefulChild.data.index}" }
                earliestUsefulChild.measure(childConstraints)
            } catch (e: Exception) {
            }
            leadingChildWithLayout = earliestUsefulChild
            trailingChildWithLayout = earliestUsefulChild
        }

        var inLayoutRange = true
        var child: LayoutNode? = earliestUsefulChild
        var index = child!!.data.index
        var endScrollPosition = child!!.data.layoutOffset + child!!.paintSize()

        fun advance(): Boolean {
            if (child == trailingChildWithLayout) inLayoutRange = false
            child = childAfter(child!!)
            if (child == null) inLayoutRange = false
            index += 1
            d {
                "advance trailing with index ${trailingChildWithLayout?.data?.index} " +
                        "current child index ${child?.data?.index} " +
                        "in layout range $inLayoutRange " +
                        "child $child " +
                        "index $index " +
                        "end scroll position $endScrollPosition"
            }
            if (!inLayoutRange) {
                if (child == null || child!!.data.index != index) {
                    child = insertAndLayoutChild(trailingChildWithLayout!!, childConstraints)
                    if (child == null) {
                        d { "run out of children" }
                        return false
                    }
                } else {
                    d { "measure child $index" }
                    child!!.measure(childConstraints)
                }
                trailingChildWithLayout = child
            }
            val childParentData = child!!.data
            childParentData.layoutOffset = endScrollPosition
            endScrollPosition += child!!.paintSize()
            return true
        }

        while (endScrollPosition < scrollPosition) {
            leadingGarbage += 1

            d { "end scroll position $endScrollPosition is lesser than scroll position $scrollPosition" }
            if (!advance()) {
                collectGarbage(leadingGarbage - 1, 0)

                val size =
                    lastChild()!!.data.layoutOffset + lastChild()!!.paintSize()
                d { "cannot advance more size is $size" }
                val geometry = SliverGeometry(
                    scrollSize = size,
                    paintSize = Px.Zero,
                    maxPaintSize = size
                )

                check(layoutNode.layoutChildren.none { it.needsRemeasure })
                return@Sliver layout(geometry) { place(geometry) }
            }
        }

        while (endScrollPosition < targetEndScrollPosition) {
            d { "end scroll position $endScrollPosition is lesser than target end scroll position $targetEndScrollPosition" }
            if (!advance()) {
                reachedEnd = true
                break
            }
        }

        if (child != null) {
            child = childAfter(child!!)
            while (child != null) {
                trailingGarbage += 1
                child = childAfter(child!!)
            }
        }

        collectGarbage(leadingGarbage, trailingGarbage)

        val estimatedMaxScrollPosition = if (reachedEnd) {
            endScrollPosition
        } else {
            Px.Infinity // todo
        }

        val paintSize = calculatePaintSize(
            constraints = constraints,
            from = firstChild()!!.data.layoutOffset,
            to = endScrollPosition
        )

        val cacheSize = calculateCacheSize(
            constraints,
            from = firstChild()!!.data.layoutOffset,
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

        check(layoutNode.layoutChildren.none { it.needsRemeasure }) {
            "Following layout nodes needs measure ${layoutNode.layoutChildren.filter { it.needsRemeasure }.map { it.data.index }}"
        }

        layout(geometry) { place(geometry) }

    }
}

fun calculatePaintSize(
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

fun calculateCacheSize(
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

private val LayoutNode.data: Data
    get() {
        val parentData = (parentData as ChildManagerParentData)
        if (parentData.data == null) parentData.data = Data(-1, Px.Zero)
        return parentData.data as Data
    }

private data class Data(var index: Int, var layoutOffset: Px)