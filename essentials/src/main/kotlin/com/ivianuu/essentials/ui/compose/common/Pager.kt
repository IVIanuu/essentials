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

import androidx.animation.PhysicsBuilder
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Constraints
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.Px
import androidx.ui.core.WithConstraints
import androidx.ui.foundation.animation.AnchorsFlingConfig
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.core.composable
import kotlin.math.absoluteValue

// todo remove once original is useable

// todo is this a good name?
// todo use page instead of item for name

@Composable
fun <T> Pager(
    items: List<T>,
    position: PagerPosition = +memo { PagerPosition(items.size) },
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

    WithConstraints { constraints ->
        Scroller(
            scrollerPosition = position.scrollerPosition,
            onScrollPositionChanged = position.onScrollerPositionChanged,
            direction = direction,
            isScrollable = true // todo make toggleable
        ) {
            val pageSize = when (direction) {
                Axis.Vertical -> constraints.maxHeight
                Axis.Horizontal -> constraints.maxWidth
            }
            PagerLayout(direction = direction, pageSize = pageSize) {
                (0 until position.pageCount).forEach { index ->
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
        get() =
            (scrollerPosition.value.value.absoluteValue / pageSize.value).toInt()

    internal var onPageChanged: ((Int) -> Unit)? = null

    private var pageSize = Px.Zero
    val onScrollerPositionChanged: (Px, Px) -> Unit = { position, maxPosition ->
        pageSize = maxPosition / (pageCount - 1)
        scrollerPosition.value = position
    }

    init {
        scrollerPosition.flingConfigFactory = {
            //val lowerAnchor = -(pageSize * currentPage)
            //val upperAnchor = -(pageSize * (min(pageCount, currentPage + 1)))
            AnchorsFlingConfig(
                anchors = (0 until pageCount).map { -(pageSize * it).value },
                animationBuilder = PhysicsBuilder(
                    stiffness = 300f
                )
            )
        }
    }

    // todo matching animation overload
    fun animateToPage(page: Int) {
        scrollerPosition.smoothScrollTo(positionFromPage(coercePage(page)))
    }

    fun snapToPage(page: Int) {
        scrollerPosition.scrollTo(positionFromPage(coercePage(page)))
    }

    fun nextPage() {
        animateToPage(currentPage + 1)
    }

    fun previousPage() {
        animateToPage(currentPage - 1)
    }

    private fun coercePage(page: Int) = page.coerceIn(0, pageCount)

    private fun positionFromPage(page: Int) = -(pageSize * page)

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
}