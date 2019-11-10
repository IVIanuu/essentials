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

import androidx.animation.AnimatedFloat
import androidx.animation.AnimationEndReason
import androidx.animation.PhysicsBuilder
import androidx.animation.ValueHolder
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.IntPx
import androidx.ui.core.ipx
import androidx.ui.foundation.animation.AnchorsFlingConfig
import androidx.ui.foundation.animation.fling
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.DragValueController
import androidx.ui.foundation.gestures.Draggable
import androidx.ui.layout.Column
import androidx.ui.layout.Expanded
import androidx.ui.layout.Row
import androidx.ui.lerp
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.layout.Expand
import com.ivianuu.essentials.ui.compose.layout.SingleChildLayout
import kotlin.math.absoluteValue

// todo is this a good name?
// todo use page instead of item for name
// todo base on scroller

@Composable
fun <T> Pager(
    items: List<T>,
    state: PagerState = +memo { PagerState(items.size) },
    onPageChanged: ((Int) -> Unit)? = null,
    direction: Axis = Axis.Horizontal,
    item: @Composable() (Int, T) -> Unit
) = composable("Pager") {
    Pager(
        state = state,
        onPageChanged = onPageChanged,
        direction = direction
    ) {
        item(it, items[it])
    }
}

@Composable
fun Pager(
    state: PagerState,
    onPageChanged: ((Int) -> Unit)? = null,
    direction: Axis = Axis.Horizontal,
    item: @Composable() (Int) -> Unit
) = composable("Pager") {
    state.onPageChanged = onPageChanged

    Draggable(
        dragDirection = when (direction) {
            Axis.Vertical -> DragDirection.Vertical
            Axis.Horizontal -> DragDirection.Horizontal
        },
        minValue = -state.maxScrollPosition,
        maxValue = 0f,
        valueController = state.controller
    ) {
        PagerLayout(
            state = state,
            direction = direction
        ) {
            val children: @Composable() () -> Unit = {
                (0 until state.pageCount).forEach { index ->
                    Expand {
                        item(index)
                    }
                }
            }
            when (direction) {
                Axis.Vertical -> {
                    Column(modifier = Expanded) {
                        children()
                    }
                }
                Axis.Horizontal -> {
                    Row(modifier = Expanded) {
                        children()
                    }
                }
            }
        }
    }
}

// todo rename to PagerController
// todo add onScrollPositionChanged
class PagerState(val pageCount: Int) {

    internal var currentScrollPosition: Float by framed(0f)
    var maxScrollPosition: Float by framed(Float.MAX_VALUE)
        internal set

    val currentPage: Int get() = (currentScrollPosition.absoluteValue / pageSize).toInt()
    private val targetPage: Int get() = (anim.targetValue.absoluteValue / pageSize).toInt()

    internal var pageSize = 0f

    internal var onPageChanged: ((Int) -> Unit)? = null
    internal var notifiedPage = 0

    private val anim = AnimatedFloat(PagePositionValueHolder(0f) {
        currentScrollPosition = it
    })

    internal val controller = object : DragValueController {

        override val currentValue
            get() = currentScrollPosition

        override fun setBounds(min: Float, max: Float) {
            anim.setBounds(min, max)
        }

        override fun onDrag(target: Float) {
            anim.snapTo(target)
        }

        override fun onDragEnd(velocity: Float, onValueSettled: (Float) -> Unit) {
            //val lowerAnchor = -(pageSize * currentPage)
            //val upperAnchor = -(pageSize * (min(pageCount, currentPage + 1)))
            val flingConfig = AnchorsFlingConfig(
                anchors = (0 until pageCount).map { -(it * pageSize) },
                animationBuilder = PhysicsBuilder(
                    stiffness = 300f
                )
            )
            val config = flingConfig.copy(
                onAnimationEnd = { endReason, value, finalVelocity ->
                    if (endReason != AnimationEndReason.Interrupted) onValueSettled(value)
                    notifyChangeIfNeeded()
                })
            anim.fling(config, velocity)
        }
    }

    fun animateToPage(page: Int) {
        val finalPage = coercePage(page)
        if (currentPage != finalPage && targetPage != finalPage) {
            anim.animateTo(positionFromPage(finalPage), onEnd = { _, _ ->
                notifyChangeIfNeeded()
            })
        }
    }

    fun snapToPage(page: Int) {
        anim.snapTo(positionFromPage(coercePage(page)))
        notifyChangeIfNeeded()
    }

    // todo nextPage, previousPage

    private fun coercePage(page: Int) = page.coerceIn(0, pageCount)

    private fun positionFromPage(page: Int) = -(pageSize * page)

    private fun notifyChangeIfNeeded() {
        if (!anim.isRunning && notifiedPage != currentPage) {
            d { "notify page changed $currentPage" }
            notifiedPage = currentPage
            onPageChanged?.invoke(currentPage)
        }
    }

}

@Composable
private fun PagerLayout(
    state: PagerState,
    direction: Axis,
    child: @Composable() () -> Unit
) = composable("PagerLayout") {
    SingleChildLayout(child = child) { measurable, constraints ->
        val placeable = measurable?.measure(
            constraints.copy(
                minWidth = constraints.maxWidth,
                minHeight = constraints.maxHeight
            )
        )

        layout(constraints.maxWidth, constraints.maxHeight) {
            state.pageSize = constraints.maxWidth.value.toFloat()

            val newMaxSize = constraints.maxWidth.value.toFloat() * (state.pageCount - 1)
            if (state.maxScrollPosition != newMaxSize) {
                state.maxScrollPosition = newMaxSize
            }

            if (placeable != null) {
                val x: IntPx
                val y: IntPx


                when (direction) {
                    Axis.Vertical -> {
                        x = IntPx.Zero
                        y = state.currentScrollPosition.toInt().ipx
                    }
                    Axis.Horizontal -> {
                        x = state.currentScrollPosition.toInt().ipx
                        y = IntPx.Zero
                    }
                }

                placeable.place(x, y)
            }
        }
    }
}

private class PagePositionValueHolder(
    var current: Float,
    val onValueChanged: (Float) -> Unit
) : ValueHolder<Float> {
    override val interpolator: (start: Float, end: Float, fraction: Float) -> Float = ::lerp
    override var value: Float
        get() = current
        set(value) {
            current = value
            onValueChanged(value)
        }
}