/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package com.ivianuu.essentials.ui.compose.common

/**

// todo remove once original is useable

// todo is this a good name?
// todo use page instead of item for name
// todo listeners
// todo add pageSnapping option

@Composable
fun <T> Pager(
    items: List<T>,
    position: PagerPosition = +memo {
        PagerPosition(
            items.size
        )
    },
    onPageChanged: ((Int) -> Unit)? = null,
    direction: Axis = Axis.Horizontal,
    item: @Composable() (Int, T) -> Unit
) = composable("Pager") {
    Pager(
        position = position,
        onPageChanged = onPageChanged,
        direction = direction
    ) {
        item(it, items[it])
    }
}

@Composable
fun Pager(
    position: PagerPosition,
    onPageChanged: ((Int) -> Unit)? = null,
    direction: Axis = Axis.Horizontal,
    item: @Composable() (Int) -> Unit
) = composable("Pager") {
    position.onPageChanged = onPageChanged

    Scroller(
        scrollerPosition = position.scrollerPosition,
        onScrollStarted = position.onScrollStarted,
        onScrollPositionChanged = position.onScrollerPositionChanged,
        onScrollEnded = position.onScrollEnded,
        direction = direction,
        isScrollable = true // todo make toggleable
    ) {
        val pageSize = +state { Px.Zero }
        OnPositioned {
            pageSize.value = when (direction) {
                Axis.Vertical -> it.size.height
                Axis.Horizontal -> it.size.width
            }
        }
        PagerLayout(
            direction = direction,
            pageSize = pageSize.value.round()
        ) {
            (0 until position.pageCount).forEach { index ->
                RepaintBoundary {
                    item(index)
                }
            }
        }
    }
}

// todo add onScrollPositionChanged
class PagerPosition(
    val pageCount: Int,
    val scrollerPosition: ScrollerPosition = ScrollerPosition()
) {

    val currentPage: Int
        get() = pageFromPosition(scrollerPosition.value)

    private var initialPosition = Px.Zero
    private val initialPage: Int get() = pageFromPosition(initialPosition)

    internal var onPageChanged: ((Int) -> Unit)? = null

var pageSize: Px = Px.Zero
internal set

    private var pageSize = Px.Zero

    init {
        scrollerPosition.flingConfigFactory = { velocity ->
            val targetPage = if (velocity > Px.Zero) coercePage(initialPage - 1)
            else coercePage(initialPage + 1)

            val anchors = listOf(pageStartPositionFromPage(targetPage).value)
            AnchorsFlingConfig(
                anchors = anchors,
                animationBuilder = PhysicsBuilder(stiffness = 50f)
            )
        }
    }

    // todo matching animation overload
    fun animateToPage(page: Int) {
        scrollerPosition.smoothScrollTo(pageStartPositionFromPage(coercePage(page)))
    }

    fun snapToPage(page: Int) {
        scrollerPosition.scrollTo(pageStartPositionFromPage(coercePage(page)))
    }

    fun nextPage() {
        animateToPage(currentPage + 1)
    }

    fun previousPage() {
        animateToPage(currentPage - 1)
    }

    private fun coercePage(page: Int) = page.coerceIn(0, pageCount - 1)

    private fun pageStartPositionFromPage(page: Int) = -(pageSize * page)
    private fun pageFromPosition(position: Px): Int = ((position + pageSize / 2) / pageSize).toInt()

    //internal var notifiedPage = 0
    private fun notifyChangeIfNeeded() {
        /*if (!anim.isRunning && notifiedPage != currentPage) {
            d { "notify page changed $currentPage" }
            notifiedPage = currentPage
            onPageChanged?.invoke(currentPage)
        }*/
        // todo
    }

}

@Composable
private fun PagerLayout(
    direction: Axis,
    pageSize: IntPx,
    children: @Composable() () -> Unit
) = composable("Pagerlayout") {
    Layout(children = children) { measureables, constraints ->
        val childConstraints = when (direction) {
            Axis.Vertical -> Constraints.tightConstraints(
                width = constraints.maxWidth,
                height = pageSize
            )
            Axis.Horizontal -> Constraints.tightConstraints(
                width = pageSize,
                height = constraints.maxHeight
            )
        }

        val placeables = measureables.map {
            it.measure(childConstraints)
        }

        val width: IntPx
        val height: IntPx
        when (direction) {
            Axis.Vertical -> {
                width = constraints.maxWidth
                height = pageSize * measureables.size
            }
            Axis.Horizontal -> {
                width = pageSize * measureables.size
                height = constraints.maxHeight
            }
        }

        layout(width, height) {
            var offset = IntPx.Zero
            placeables.forEach { placeable ->
                when (direction) {
                    Axis.Vertical -> {
                        placeable.place(IntPx.Zero, offset)
                        offset += placeable.height
                    }
                    Axis.Horizontal -> {
                        placeable.place(offset, IntPx.Zero)
                        offset += placeable.width
                    }
                }
            }
        }
    }
}*/