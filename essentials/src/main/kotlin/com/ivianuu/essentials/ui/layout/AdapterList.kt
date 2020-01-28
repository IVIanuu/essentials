package com.ivianuu.essentials.ui.layout

import androidx.compose.Composable
import androidx.compose.Compose
import androidx.compose.CompositionReference
import androidx.compose.Context
import androidx.compose.FrameManager
import androidx.compose.composer
import androidx.compose.compositionReference
import androidx.compose.remember
import androidx.ui.core.Clip
import androidx.ui.core.Constraints
import androidx.ui.core.ContextAmbient
import androidx.ui.core.LayoutNode
import androidx.ui.core.Measurable
import androidx.ui.core.MeasureScope
import androidx.ui.core.Modifier
import androidx.ui.core.Ref
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.TouchSlopDragGestureDetector
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.unit.IntPx
import androidx.ui.unit.Px
import androidx.ui.unit.PxPosition
import androidx.ui.unit.ipx
import androidx.ui.unit.px
import androidx.ui.unit.round
import androidx.ui.unit.toPx

private class ListState<T>(
    var itemCallback: @Composable() (T) -> Unit,
    var data: List<T>
) : DragObserver {
    var forceRecompose = false
    var compositionRef: CompositionReference? = null
    /**
     * Should always be non-null when attached
     */
    var context: Context? = null
    /**
     * Used to get the reference to populate [rootNode]
     */
    val rootNodeRef = Ref<LayoutNode>()
    /**
     * The root [LayoutNode] of this [AdapterList]
     */
    val rootNode get() = rootNodeRef.value!!
    /**
     * The measure blocks for [rootNode]
     */
    val measureBlocks = ListMeasureBlocks()
    /**
     * The index of the first displayed item as of the last pass
     */
    var firstDisplayedItem = 0
    /**
     * The index of the first item that should be displayed, regardless of what is currently
     * displayed
     */
    var itemIndexOffset = 0
    /**
     * Scrolling forward is positive - i.e., the amount that the item is offset backwards
     */
    var firstItemScrollOffset = 0.px
    /**
     * The amount
     */
    var lastItemRemainingSpace = 0.px
//    /**
//     * Scrolling forward is positive
//     */
//    var scrollToBeConsumed = 0.px

    override fun onDrag(dragDistance: PxPosition): PxPosition {
        // TODO(ryanmentley): we need to compose and figure out how much we _can_ consume
        var scrollToBeConsumed = dragDistance.y

        // General outline:
        // Consume as much of the drag as possible via adjusting the scroll offset
        scrollToBeConsumed = consumeScrollViaOffset(scrollToBeConsumed)

        return PxPosition(0.px, dragDistance.y - scrollToBeConsumed)

        // This may either:
        //  - scroll an item entirely offscreen, thus requiring changing the first item
        //  - require another item to be brought onscreen, requiring verification that we have
        //    enough space worth of additional items, and potentially composition of that new item
        // To determine this, find which one can be scrolled less: the first item, or the last item
        // If that consumes the entire drag: adjust the scroll offset and we're done
        // Other
        // Can the first/last item consume this drag entirely by scrolling?
        // If so, adjust the scroll offset to make that happen
        // Otherwise, we need to compose more
        // Are there more items in the backing data?
    }

    /**
     * @return The amount of scroll remaining unconsumed
     */
    private fun consumeScrollViaOffset(delta: Px): Px {
        if (delta < 0.px) {
            // Scrolling forward, content moves up
            // Consume via space at end
            // Remember: delta is *negative*
            if (lastItemRemainingSpace >= -delta) {
                // We can consume it all
                updateScrollOffsets(delta)
                return 0.px
            } else {
                // All offset consumed, return the remaining offset to the caller
                // delta is negative, prevRemainingSpace/lastItemRemainingSpace are positive
                val prevRemainingSpace = lastItemRemainingSpace
                updateScrollOffsets(-prevRemainingSpace)
                return delta + prevRemainingSpace
            }
        } else {
            // Scrolling backward, content moves down
            // Consume via initial offset
            if (firstItemScrollOffset >= delta) {
                // We can consume it all
                updateScrollOffsets(delta)
                return 0.px
            } else {
                // All offset consumed, return the remaining offset to the caller
                val prevRemainingSpace = firstItemScrollOffset
                updateScrollOffsets(prevRemainingSpace)
                return delta - prevRemainingSpace
            }
        }
    }

    /**
     * Does no bounds checking, just moves the start and last offsets in sync.
     * Assumes the caller has checked bounds.
     */
    private fun updateScrollOffsets(delta: Px) {
        if (delta == 0.px) {
            return
        }
        // Scrolling forward is negative delta and consumes space, so add the negative
        lastItemRemainingSpace += delta
        // Scrolling forward is negative delta and adds offset, so subtract the negative
        firstItemScrollOffset -= delta
        rootNode.requestRemeasure()
    }

    private inner class ListMeasureBlocks : LayoutNode.NoIntrinsicsMeasureBlocks(
        error = "Intrinsic measurements are not supported by AdapterList (yet)"
    ) {
        override fun measure(
            measureScope: MeasureScope,
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureScope.LayoutResult {
            if (forceRecompose) {
                rootNode.ignoreModelReads { recomposeAllChildren() }
                // if there were models created and read inside this subcomposition
                // and we are going to modify these models within the same frame,
                // the composables which read this model will not be recomposed.
                // to make this possible we should switch to the next frame.
                FrameManager.nextFrame()
            }

            val width: IntPx = constraints.maxWidth
            val height: IntPx = constraints.maxHeight
            // TODO: axis
            val childConstraints = Constraints(maxWidth = width, maxHeight = IntPx.Infinity)

            val layoutChildren = rootNode.layoutChildren

            // Remove no-longer-needed items
            if (itemIndexOffset > firstDisplayedItem) {
                rootNode.emitRemoveAt(0, itemIndexOffset - firstDisplayedItem)
            }
            firstDisplayedItem = itemIndexOffset

            var heightUsed = -firstItemScrollOffset.round()
            var index = itemIndexOffset
            while (heightUsed < height && index < data.size) {
                val node = getNode(index)
                node.measure(childConstraints)
                heightUsed += node.height
                index++
            }

            lastItemRemainingSpace = if (heightUsed > height) {
                (heightUsed - height).toPx()
            } else {
                0.px
            }

            // Remove remaining LayoutNodes
            val layoutChildrenInNode = rootNode.layoutChildren.size
            if (index < layoutChildrenInNode) {
                rootNode.emitRemoveAt(index, layoutChildrenInNode - index)
            }

            return measureScope.layout(width = width, height = height) {
                var currentY = -firstItemScrollOffset.round()
                layoutChildren.forEach {
                    it.place(x = 0.ipx, y = currentY)
                    currentY += it.height
                }
            }
        }
    }

    fun recomposeIfAttached() {
        if (rootNode.owner != null) {
            recomposeAllChildren()
        }
    }

    private fun recomposeAllChildren() {
        for (idx in rootNode.layoutChildren.indices) {
            composeChild(idx)
        }
        forceRecompose = false
    }

    private fun getNode(index: Int): LayoutNode {
        val layoutChildren = rootNode.layoutChildren
        val numChildren = layoutChildren.size
        check(index <= numChildren) {
            "Index too high: $index, current children: $numChildren"
        }
        return if (index < numChildren) {
            layoutChildren[index]
        } else {
            composeChild(index)
        }
    }

    private fun composeChild(dataIndex: Int): LayoutNode {
        val node: LayoutNode
        if (rootNode.layoutChildren.size == dataIndex) {
            // This is a new node
            node = LayoutNode()
            // TODO this is stolen from Column, and is also a hack
            node.measureBlocks =
                MeasuringIntrinsicsMeasureBlocks { measurables, constraints ->
                    val placeables = measurables.map { measurable ->
                        measurable.measure(
                            Constraints(
                                minWidth = constraints.minWidth,
                                maxWidth = constraints.maxWidth
                            )
                        )
                    }
                    val columnWidth = (placeables.maxBy { it.width.value }?.width ?: 0.ipx)
                        .coerceAtLeast(constraints.minWidth)
                    val columnHeight = placeables.sumBy { it.height.value }.ipx.coerceIn(
                        constraints.minHeight,
                        constraints.maxHeight
                    )
                    layout(columnWidth, columnHeight) {
                        var top = 0.ipx
                        placeables.forEach { placeable ->
                            placeable.place(0.ipx, top)
                            top += placeable.height
                        }
                    }
                }
            rootNode.emitInsertAt(dataIndex, node)
        } else {
            node = rootNode.layoutChildren[dataIndex]
        }
        Compose.subcomposeInto(node, context!!, compositionRef) {
            itemCallback(data[dataIndex])
        }
        return node
    }
}

@Composable
fun <T> AdapterList(
    data: List<T>,
    modifier: Modifier = Modifier.None,
    itemCallback: @Composable() (T) -> Unit
) {
    val state = remember {
        ListState(
            data = data,
            itemCallback = itemCallback
        )
    }
    state.itemCallback = itemCallback
    state.data = data
    state.context = ContextAmbient.current
    state.compositionRef = compositionReference()
    state.forceRecompose = true

    TouchSlopDragGestureDetector(dragObserver = state) {
        Clip(shape = RectangleShape) {
            composer.emit<LayoutNode>(
                key = 0,
                ctor = { LayoutNode() },
                update = {
                    node.modifier = modifier
                    node.ref = state.rootNodeRef
                    node.measureBlocks = state.measureBlocks
                }
            )
        }
    }
    state.recomposeIfAttached()
}
