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
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.px
import androidx.ui.core.toPx
import androidx.ui.foundation.animation.AnchorsFlingConfig
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollPosition
import com.ivianuu.essentials.ui.compose.common.scrolling.Scrollable
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.invokeAsComposable
import com.ivianuu.essentials.ui.compose.core.remember
import com.ivianuu.essentials.ui.compose.layout.NonNullSingleChildLayout
import kotlin.math.roundToInt

// todo disable
// todo remove once original is useable

@Composable
fun <T> Pager(
    items: List<T>,
    position: PagerPosition = remember { PagerPosition(items.size) },
    direction: Axis = Axis.Horizontal,
    reverse: Boolean = false,
    item: @Composable() (Int, T) -> Unit
) = composable {
    Pager(
        position = position,
        direction = direction,
        reverse = reverse
    ) {
        item.invokeAsComposable(it, items[it])
    }
}

@Composable
fun Pager(
    position: PagerPosition,
    direction: Axis = Axis.Horizontal,
    reverse: Boolean = false,
    item: @Composable() (Int) -> Unit
) = composable {
    position.scrollPositionChanged()

    PagerLayout(
        direction = direction,
        viewportSize = position.viewportSize,
        onViewportSizeChanged = { position.viewportSize = it }
    ) {
        Scrollable(
            position = position.scrollPosition,
            direction = direction
        ) {
            /*Viewport(
                position = position.scrollPosition,
                mainAxisDirection = when (direction) {
                    Axis.Horizontal -> if (reverse) Direction.LEFT else Direction.RIGHT
                    Axis.Vertical -> if (reverse) Direction.UP else Direction.DOWN
                }
            ) {
                SliverList(
                    count = position.pageCount,
                    itemSizeProvider = { _, constraints -> constraints.viewportMainAxisSpace },
                    item = item
                )
            }*/
        }
    }
}

class PagerPosition(
    val pageCount: Int,
    initialPage: Int? = null,
    val scrollPosition: ScrollPosition = ScrollPosition()
) {

    internal var viewportSize: Px
        get() = _viewportSize
        set(value) {
            _viewportSize = value
            if (_initialPage != null) {
                animateToPage(_initialPage!!)
                _initialPage = null
            }
        }

    private var _viewportSize by framed(Px.Zero)

    private var _initialPage: Int? = initialPage

    private var _current by framed(0)
    val current: Int
        get() = _current

    init {
        scrollPosition.flingConfigFactory = { velocity ->
            var page = current.toFloat()
            if (velocity < (-1000).px) page -= 0.5f
            else if (velocity > 1000.px) page += 0.5f

            AnchorsFlingConfig(
                anchors = listOf(positionFromPage(page.roundToInt()).value),
                animationBuilder = PhysicsBuilder(
                    stiffness = 3500f
                )
            )
        }
    }

    internal fun scrollPositionChanged() {
        val newPage = pageFromPosition(scrollPosition.value)
        d { "position ${scrollPosition.value} page $newPage" }
        if (_current != newPage) {
            _current = newPage
        }
    }

    fun animateToPage(value: Int) {
        scrollPosition.smoothScrollTo(
            positionFromPage(value), PhysicsBuilder(
                stiffness = 3500f
            )
        )
    }

    private fun pageFromPosition(position: Px) = (position / viewportSize).toInt()
    private fun positionFromPage(page: Int) = viewportSize * page
}

@Composable
private fun PagerLayout(
    direction: Axis,
    viewportSize: Px,
    onViewportSizeChanged: (Px) -> Unit,
    child: @Composable() () -> Unit
) = composable {
    NonNullSingleChildLayout(child) { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            val newViewportSize = when (direction) {
                Axis.Horizontal -> placeable.width.toPx()
                Axis.Vertical -> placeable.height.toPx()
            }

            if (newViewportSize != viewportSize) {
                onViewportSizeChanged(newViewportSize)
            }

            placeable.place(PxPosition.Origin)
        }
    }
}