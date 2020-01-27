package com.ivianuu.essentials.ui.layout

import androidx.compose.Composable
import androidx.compose.Compose
import androidx.compose.CompositionReference
import androidx.compose.Context
import androidx.compose.FrameManager
import androidx.compose.Observe
import androidx.compose.composer
import androidx.compose.compositionReference
import androidx.compose.onDispose
import androidx.compose.remember
import androidx.ui.core.Clip
import androidx.ui.core.Constraints
import androidx.ui.core.ContextAmbient
import androidx.ui.core.LayoutNode
import androidx.ui.core.LayoutNodeAccessor
import androidx.ui.core.Measurable
import androidx.ui.core.MeasureScope
import androidx.ui.core.Modifier
import androidx.ui.core.Ref
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.unit.IntPx
import androidx.ui.unit.Px
import androidx.ui.unit.ipx
import androidx.ui.unit.max
import androidx.ui.unit.round
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.common.ScrollPosition
import com.ivianuu.essentials.ui.common.Scrollable
import com.ivianuu.essentials.ui.core.Axis
import com.ivianuu.essentials.ui.core.retain

@Composable
fun <T> ScrollableList(
    items: List<T>,
    modifier: Modifier = Modifier.None,
    direction: Axis = Axis.Vertical,
    position: ScrollPosition = retain { ScrollPosition() },
    enabled: Boolean = true,
    item: @Composable (Int, T) -> Unit
) {
    ScrollableList(
        itemCount = items.size,
        modifier = modifier,
        direction = direction,
        position = position,
        enabled = enabled,
        item = { item(it, items[it]) }
    )
}

@Composable
fun ScrollableList(
    itemCount: Int,
    modifier: Modifier = Modifier.None,
    direction: Axis = Axis.Vertical,
    position: ScrollPosition = retain { ScrollPosition() },
    enabled: Boolean = true,
    item: @Composable (Int) -> Unit
) {
    ScrollableList(
        modifier = modifier,
        direction = direction,
        position = position,
        enabled = enabled,
        itemFactory = { index ->
            if (index in 0 until itemCount) (@Composable { item(index) })
            else null
        }
    )
}

@Composable
fun ScrollableList(
    modifier: Modifier = Modifier.None,
    direction: Axis = Axis.Vertical,
    position: ScrollPosition = retain { ScrollPosition() },
    enabled: Boolean = true,
    itemFactory: (Int) -> @Composable (() -> Unit)?
) {
    val state = remember { ScrollableListState() }
    state.position = position
    state.composableFactory = itemFactory
    state.context = ContextAmbient.current
    state.compositionRef = compositionReference()
    state.forceRecompose = true

    Scrollable(
        position = state.position,
        direction = direction,
        enabled = enabled
    ) {
        onDispose { state.dispose() }

        Observe {
            position.value
            state.onScroll()
        }

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

private class ScrollableListState {

    val rootNodeRef = Ref<LayoutNode>()
    var forceRecompose = false
    lateinit var compositionRef: CompositionReference
    lateinit var context: Context
    lateinit var position: ScrollPosition

    val rootNode get() = rootNodeRef.value!!

    val measureBlocks = ListMeasureBlocks()

    var composableFactory: (Int) -> @Composable (() -> Unit)? = { null }
        set(value) {
            if (field == value) return
            field = value
            if (rootNodeRef.value != null) {
                for (child in rootNode.layoutChildren.toList()) {
                    val newComposable = composableFactory(child.state.index)
                    if (newComposable != null) {
                        child.state.composable = newComposable
                    } else {
                        removeChild(child)
                    }
                }
            }
        }

    private val childStates = mutableMapOf<LayoutNode, ChildState>()

    private val LayoutNode.state get() = childStates.getValue(this)

    private var version = 0

    fun onScroll() {
        rootNodeRef.value?.requestRemeasure()
    }

    fun dispose() {
        childStates.values.forEach { it.dispose() }
    }

    fun recomposeIfAttached() {
        if (rootNodeRef.value?.owner != null) {
            recomposeAllChildren()
        }
    }

    private fun recomposeAllChildren() {
        rootNode.layoutChildren.forEach {
            it.state.compose()
        }
        forceRecompose = false
    }

    private inner class ListMeasureBlocks : LayoutNode.NoIntrinsicsMeasureBlocks("Unsupported") {
        override fun measure(
            measureScope: MeasureScope,
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureScope.LayoutResult {
            version++

            if (forceRecompose) {
                rootNode.ignoreModelReads { recomposeAllChildren() }
                // if there were models created and read inside this subcomposition
                // and we are going to modify these models within the same frame,
                // the composables which read this model will not be recomposed.
                // to make this possible we should switch to the next frame.
                FrameManager.nextFrame()
            }

            lateinit var scrollPosition: Px
            rootNode.ignoreModelReads { scrollPosition = position.value }

            d { "begin measure $version scroll pos $scrollPosition constraints $constraints" }

            // todo direction
            val viewportMainAxisSize = constraints.maxHeight
            val viewportCrossAxisSize = constraints.maxWidth

            // todo direction
            val childConstraints = Constraints.fixedWidth(viewportCrossAxisSize)

            val cacheSize = viewportMainAxisSize / 2

            val targetEndScrollPosition = scrollPosition + viewportMainAxisSize + cacheSize

            var leadingGarbage = 0
            var trailingGarbage = 0
            var reachedEnd = false

            if (rootNode.layoutChildren.isEmpty()) {
                if (addChild(0) == null) {
                    d { "end measure $version with no children" }
                    // There are no children.
                    return doLayout(
                        measureScope,
                        viewportCrossAxisSize,
                        viewportMainAxisSize,
                        scrollPosition
                    )
                }
            }

            var leadingChildWithLayout: LayoutNode? = null
            var trailingChildWithLayout: LayoutNode? = null

            var earliestUsefulChild = rootNode.layoutChildren.minBy { it.state.index }
            var earliestScrollPosition = earliestUsefulChild!!.state.layoutOffset

            while (earliestScrollPosition > scrollPosition) {
                earliestUsefulChild = insertAndLayoutLeadingChild(childConstraints)

                if (earliestUsefulChild == null) {
                    val firstChild = firstChild!!
                    firstChild.state.layoutOffset = Px.Zero

                    if (scrollPosition == Px.Zero) {
                        earliestUsefulChild = firstChild
                        leadingChildWithLayout = earliestUsefulChild
                        trailingChildWithLayout = earliestUsefulChild
                        break
                    } else {
                        // todo ignore model reads
                        rootNode.ignoreModelReads {
                            position.correctBy(-scrollPosition)
                        }
                        d { "end measure $version ran out of children with correction $scrollPosition" }
                        return doLayout(
                            measureScope,
                            viewportCrossAxisSize,
                            viewportMainAxisSize,
                            scrollPosition
                        )
                    }
                }

                val firstChildScrollPosition = earliestScrollPosition - firstChild!!.height
                earliestUsefulChild.state.layoutOffset = firstChildScrollPosition
                leadingChildWithLayout = earliestUsefulChild
                trailingChildWithLayout = earliestUsefulChild
                earliestScrollPosition = earliestUsefulChild.state.layoutOffset
            }

            if (leadingChildWithLayout == null) {
                earliestUsefulChild!!.doMeasure(
                    childConstraints,
                    "leading child with layout was null"
                )
                leadingChildWithLayout = earliestUsefulChild
                trailingChildWithLayout = earliestUsefulChild
            }

            var inLayoutRange = true
            var child = earliestUsefulChild
            var index = child!!.state.index
            var endScrollPosition = child.state.layoutOffset + child.height
            fun advance(src: String): Boolean {
                if (child == trailingChildWithLayout) inLayoutRange = false
                child = childAfter(child!!)
                if (child == null) inLayoutRange = false
                index += 1
                if (!inLayoutRange) {
                    if (child == null || child!!.state.index != index) {
                        // We are missing a child. Insert it (and lay it out) if possible.
                        child = addChild(trailingChildWithLayout!!.state.index + 1)
                        child?.doMeasure(childConstraints, "advance new child $src")
                        if (child == null) {
                            // We have run out of children.
                            return false
                        }
                    } else {
                        // Lay out the child.
                        child!!.doMeasure(childConstraints, "advance not null $src")
                    }
                    trailingChildWithLayout = child
                }
                child!!.state.layoutOffset = endScrollPosition
                endScrollPosition = child!!.state.layoutOffset + child!!.height
                return true
            }

            while (endScrollPosition < scrollPosition) {
                leadingGarbage += 1
                if (!advance("first child that ends")) {
                    collectGarbage(leadingGarbage - 1, 0)
                    d { "end measure $version todo" }
                    return doLayout(
                        measureScope,
                        viewportCrossAxisSize,
                        viewportMainAxisSize,
                        scrollPosition
                    )
                }
            }

            while (endScrollPosition < targetEndScrollPosition) {
                if (!advance("first child after our end")) {
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

            rootNode.ignoreModelReads {
                val estimatedMaxScrollPosition = if (reachedEnd) max(
                    endScrollPosition - viewportMainAxisSize,
                    Px.Zero
                ) else Px.Infinity
                if (estimatedMaxScrollPosition != position.maxValue) {
                    d { "update max scroll position $estimatedMaxScrollPosition" }
                    position.updateBounds(maxValue = estimatedMaxScrollPosition)
                }
            }

            d { "end measure $version" }

            return doLayout(
                measureScope,
                viewportCrossAxisSize,
                viewportMainAxisSize,
                scrollPosition
            )
        }
    }

    private fun LayoutNode.doMeasure(constraints: Constraints, src: String) {
        if (LayoutNodeAccessor.getMeasureIteration(this) !=
            LayoutNodeAccessor.getMeasureIteration(rootNode)
        ) {
            d { "measure child ${state.index} from $src" }
            measure(constraints)
        }
    }

    private fun getChild(index: Int): LayoutNode? {
        if (index < 0) return null
        return rootNode.layoutChildren.singleOrNull { it.state.index == index }
    }

    private fun addChild(index: Int): LayoutNode? {
        val composable = composableFactory(index)
        d { "create child for $index result is success ${composable != null}" }
        if (composable == null) return null

        val childNode = LayoutNode()

        childNode.measureBlocks =
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

        rootNode.emitInsertAt(0, childNode)

        val childState = ChildState(childNode, index).apply {
            this.composable = composable
        }

        childStates[childNode] = childState

        childState.compose()

        return childNode
    }

    private fun getOrAddChild(index: Int): LayoutNode? = getChild(index) ?: addChild(index)

    private fun removeChild(child: LayoutNode) {
        child.state.dispose()
        childStates.remove(child)
        rootNode.emitRemoveAt(
            rootNode.layoutChildren.indexOf(child),
            1
        )
    }

    private fun childAfter(child: LayoutNode): LayoutNode? =
        getChild(child.state.index + 1)

    fun collectGarbage(leadingGarbage: Int, trailingGarbage: Int) {
        d { "collect garbage leading $leadingGarbage trailing $trailingGarbage" }
        repeat(leadingGarbage) {
            rootNode.layoutChildren
                .minBy { it.state.index }
                ?.let { removeChild(it) }
        }
        repeat(trailingGarbage) {
            rootNode.layoutChildren
                .maxBy { it.state.index }
                ?.let { removeChild(it) }
        }
    }

    private val firstChild get() = rootNode.layoutChildren.minBy { it.state.index }

    private fun insertAndLayoutLeadingChild(constraints: Constraints): LayoutNode? {
        val index = firstChild?.let { it.state.index - 1 } ?: 0
        d { "try insert and layout leading child at $index" }
        val child = getOrAddChild(index) ?: return null
        d { "insert and layout leading child $index" }
        child.doMeasure(constraints, "insert and layout leading")
        return child
    }

    private fun doLayout(
        measureScope: MeasureScope,
        width: IntPx,
        height: IntPx,
        position: Px
    ): MeasureScope.LayoutResult {
        // todo direction
        return measureScope.layout(width = width, height = height) {
            d { "place items ${rootNode.layoutChildren.map { it.state.index }}" }
            rootNode.layoutChildren
                .forEach { child ->
                    // todo direction
                    child.place(
                        x = IntPx.Zero,
                        y = (child.state.layoutOffset - position).round()
                    )
                }
        }
    }

    private inner class ChildState(
        val node: LayoutNode,
        val index: Int
    ) {
        var composable: @Composable () -> Unit = {}
        var layoutOffset = Px.Zero

        private val composition = Compose.subcomposeInto(node, context, compositionRef) {
            OnlyOnce(composable)
        }

        fun compose() {
            composition.compose()
        }

        fun dispose() {
            composition.dispose()
        }

        @Composable
        private fun OnlyOnce(children: @Composable () -> Unit) {
            children()
        }
    }
}


