package com.ivianuu.essentials.ui.layout

import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.key
import androidx.compose.remember
import androidx.ui.core.Constraints
import androidx.ui.core.LayoutNode
import androidx.ui.core.MeasureScope
import androidx.ui.core.Modifier
import androidx.ui.core.ParentData
import androidx.ui.core.RepaintBoundary
import androidx.ui.unit.IntPx
import androidx.ui.unit.Px
import androidx.ui.unit.max
import androidx.ui.unit.round
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.e
import com.ivianuu.essentials.ui.common.ScrollPosition
import com.ivianuu.essentials.ui.common.Scrollable
import com.ivianuu.essentials.ui.common.holder
import com.ivianuu.essentials.ui.core.Axis
import com.ivianuu.essentials.ui.core.Ignore
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

    // update state
    state.position = position
    remember(itemFactory) { state.update(itemFactory) }

    Scrollable(
        position = state.position,
        direction = direction,
        enabled = enabled
    ) {
        RepaintBoundary {
            ScrollableListLayout(
                modifier = modifier,
                state = state
            )
        }
    }
}

@Model
private class ScrollableListState {

    @Ignore
    lateinit var position: ScrollPosition

    var composableFactory: (Int) -> @Composable (() -> Unit)? = { null }

    fun update(composableFactory: (Int) -> @Composable (() -> Unit)?) {
        this.composableFactory = composableFactory
        for (item in items.toList()) {
            val newComposable = composableFactory(item.index)
            if (newComposable != null) {
                item.composable = newComposable
            } else {
                items.remove(item)
            }
        }
    }

    val items = mutableListOf<ScrollableListItem>()

    fun createItem(index: Int): ScrollableListItem? {
        val composable = composableFactory(index)
        d { "create item for $index result is success ${composable != null}" }
        if (composable == null) return null
        return ScrollableListItem(index = index, composable = composable)
    }
}

@Model
private class ScrollableListItem(
    val index: Int,
    var composable: @Composable () -> Unit
) {

    @Ignore
    var layoutOffset = Px.Zero

    @Composable
    fun compose() {
        d { "invoke item $index" }
        ParentData(data = this, children = composable)
    }

    override fun toString(): String = "ScrollableListItem(index=$index)"
}

@Composable
private fun ScrollableListLayout(
    modifier: Modifier,
    state: ScrollableListState
) {
    d { "invoke scrollable list layout" }

    val version = holder { 0 }

    ComposableLayout(
        children = {
            d { "invoke scrollable list children ${state.items.map { it.index }}" }

            state.items
                .sortedBy { it.index }
                .forEach { item ->
                    key(item.index) {
                        item.compose()
                    }
                }
        },
        modifier = modifier
    ) { constraints, getMeasurables, recompose ->
        version.value++

        d { "begin measure ${version.value} scroll pos ${state.position.value} all items ${state.items.map { it.index }} constraints $constraints" }

        // todo direction
        val viewportMainAxisSize = constraints.maxHeight
        val viewportCrossAxisSize = constraints.maxWidth

        // todo direction
        val childConstraints = Constraints.fixedWidth(viewportCrossAxisSize)

        fun LayoutNode.doMeasure(constraints: Constraints, src: String) {
            try {
                d { "measure child ${item.index} from $src" }
                measure(constraints)
            } catch (e: Exception) {
                e(e) { "measured twice ${item.index}" }
            }
        }

        fun getChildren(): List<LayoutNode> {
            val children =  getMeasurables()
                .map { it as LayoutNode }
                .filter { it.parentData is ScrollableListItem }
                .filter { it.item in state.items }
            (state.items - children.map { it.item })
                .takeIf { it.isNotEmpty() }
                ?.let { it ->

                    //error("missing layout nodes for items ${it.map { it.index }}")
                }
            return children
        }

        fun getChild(index: Int): LayoutNode? {
            if (index < 0) return null
            return getChildren().singleOrNull { it.item.index == index }
        }

        fun addChild(index: Int): LayoutNode? {
            val item = state.createItem(index)
            if (item == null) {
                d { "could not create child at $index" }
                return null
            }
            d { "created child at $index" }
            state.items.add(0, item)
            recompose()
            return getChild(index) //?: error("could not retrieve created child $index")
        }

        fun getOrAddChild(index: Int): LayoutNode? = getChild(index) ?: addChild(index)

        fun childAfter(child: LayoutNode): LayoutNode? =
            getChild(child.item.index + 1)

        fun collectGarbage(leadingGarbage: Int, trailingGarbage: Int) {
            d { "collect garbage leading $leadingGarbage trailing $trailingGarbage" }
            repeat(leadingGarbage) {
                state.items
                    .minBy { it.index }
                    ?.let { state.items.remove(it) }
            }
            repeat(trailingGarbage) {
                state.items
                    .maxBy { it.index }
                    ?.let { state.items.remove(it) }
            }
        }

        fun firstChild() = getChildren().minBy { it.item.index }

        fun insertAndLayoutLeadingChild(): LayoutNode? {
            val index = firstChild()?.let { it.item.index - 1 } ?: 0
            d { "try insert and layout leading child at $index" }
            val child = getOrAddChild(index) ?: return null
            d { "insert and layout leading child $index" }
            child.doMeasure(childConstraints, "insert and layout leading")
            return child
        }

        fun doLayout(): MeasureScope.LayoutResult {
            // todo direction
            return layout(width = viewportCrossAxisSize, height = viewportMainAxisSize) {
                val children = getChildren()
                d { "place items ${children.map { it.item.index }}" }
                children
                    .forEach { child ->
                        // todo direction
                        child.place(
                            x = IntPx.Zero,
                            y = (child.item.layoutOffset - state.position.value).round()
                        )
                    }
            }
        }

        val cacheSize = viewportMainAxisSize / 2

        val scrollPosition = state.position.value
        val targetEndScrollPosition = scrollPosition + viewportMainAxisSize + cacheSize
        
        var leadingGarbage = 0
        var trailingGarbage = 0
        var reachedEnd = false

        if (getChildren().isEmpty()) {
            if (addChild(0) == null) {
                d { "end measure ${version.value} with no children" }
                // There are no children.
                return@ComposableLayout doLayout()
            }
        }

        var leadingChildWithLayout: LayoutNode? = null
        var trailingChildWithLayout: LayoutNode? = null

        var earliestUsefulChild = getChildren().minBy { it.item.index }
        var earliestScrollPosition = earliestUsefulChild!!.item.layoutOffset

        while (earliestScrollPosition > scrollPosition) {
            earliestUsefulChild = insertAndLayoutLeadingChild()

            if (earliestUsefulChild == null) {
                val firstChild = firstChild()!!
                firstChild.item.layoutOffset = Px.Zero

                if (scrollPosition == Px.Zero) {
                    earliestUsefulChild = firstChild()
                    leadingChildWithLayout = earliestUsefulChild
                    trailingChildWithLayout = earliestUsefulChild
                    break
                } else {
                    state.position.correctBy(-scrollPosition)
                    d { "end measure ${version.value} ran out of children with correction $scrollPosition" }
                    return@ComposableLayout doLayout()
                }
            }

            val firstChildScrollPosition = earliestScrollPosition - firstChild()!!.height
            earliestUsefulChild.item.layoutOffset = firstChildScrollPosition
            leadingChildWithLayout = earliestUsefulChild
            trailingChildWithLayout = earliestUsefulChild
            earliestScrollPosition = earliestUsefulChild.item.layoutOffset
        }

        if (leadingChildWithLayout == null) {
            earliestUsefulChild!!.doMeasure(childConstraints, "leading child with layout was null")
            leadingChildWithLayout = earliestUsefulChild
            trailingChildWithLayout = earliestUsefulChild
        }

        var inLayoutRange = true
        var child = earliestUsefulChild
        var index = child!!.item.index
        var endScrollPosition = child.item.layoutOffset + child.height
        fun advance(src: String): Boolean {
            if (child == trailingChildWithLayout) inLayoutRange = false
            child = childAfter(child!!)
            if (child == null) inLayoutRange = false
            index += 1
            if (!inLayoutRange) {
                if (child == null || child!!.item.index != index) {
                    // We are missing a child. Insert it (and lay it out) if possible.
                    child = addChild(trailingChildWithLayout!!.item.index + 1)
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
            child!!.item.layoutOffset = endScrollPosition
            endScrollPosition = child!!.item.layoutOffset + child!!.height
            return true
        }

        while (endScrollPosition < scrollPosition) {
            leadingGarbage += 1
            if (!advance("first child that ends")) {
                collectGarbage(leadingGarbage - 1, 0)
                d { "end measure ${version.value} todo" }
                return@ComposableLayout doLayout()
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

        val estimatedMaxScrollPosition = if (reachedEnd) max(endScrollPosition - viewportMainAxisSize, Px.Zero) else Px.Infinity
        if (estimatedMaxScrollPosition != state.position.maxValue) {
            d { "update max scroll position $estimatedMaxScrollPosition" }
            state.position.updateBounds(maxValue = estimatedMaxScrollPosition)
        }

        d { "end measure ${version.value}" }

        return@ComposableLayout doLayout()
    }
}

private val LayoutNode.item get() = parentData as ScrollableListItem
