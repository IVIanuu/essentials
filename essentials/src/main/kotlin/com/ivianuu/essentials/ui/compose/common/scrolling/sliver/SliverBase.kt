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

package com.ivianuu.essentials.ui.compose.common.scrolling.sliver

import androidx.compose.Composable
import androidx.ui.core.Density
import androidx.ui.core.DensityScope
import androidx.ui.core.Direction
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.LayoutNode
import androidx.ui.core.Placeable
import androidx.ui.core.Px
import androidx.ui.core.round
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollDirection
import com.ivianuu.essentials.ui.compose.core.composable

class SliverChildren {

    internal val _children = mutableListOf<SliverChild>()

    fun Sliver(
        key: Any? = null,
        children: @Composable() () -> Unit,
        measureBlock: SliverMeasureBlock
    ) {
        _children += SliverChild(key, children, measureBlock)
    }

}

data class SliverChild(
    val key: Any?,
    val children: @Composable() () -> Unit,
    val measureBlock: SliverMeasureBlock
)

data class SliverConstraints(
    val mainAxisDirection: Direction,
    val growthDirection: GrowthDirection,
    val userScrollDirection: ScrollDirection,
    val scrollPosition: Px,
    val precedingScrollSpace: Px,
    val overlap: Px,
    val remainingPaintSpace: Px,
    val viewportCrossAxisSpace: Px,
    val crossAxisDirection: Direction,
    val viewportMainAxisSpace: Px,
    val remainingCacheSpace: Px,
    val cacheOrigin: Px
)

data class SliverGeometry(
    val scrollSize: Px = Px.Zero,
    val paintSize: Px = Px.Zero,
    val paintOrigin: Px = Px.Zero,
    val layoutSize: Px = paintSize,
    val maxPaintSize: Px = Px.Zero,
    val maxScrollObstructionSize: Px = Px.Zero,
    val visible: Boolean = paintSize > Px.Zero,
    val hasVisualOverflow: Boolean = false,
    val scrollOffsetCorrection: Px = Px.Zero,
    val cacheSize: Px = layoutSize
)

class SliverMeasureScope(
    override val density: Density
) : DensityScope {
    lateinit var layoutNode: LayoutNode
    fun layout(
        geometry: SliverGeometry,
        placementBlock: Placeable.PlacementScope.() -> Unit
    ): SliverLayoutResult = SliverLayoutResult(geometry, placementBlock)
}

class SliverLayoutResult(
    val geometry: SliverGeometry,
    val placementBlock: Placeable.PlacementScope.() -> Unit
)

typealias SliverMeasureBlock = SliverMeasureScope.(SliverConstraints) -> SliverLayoutResult

@Composable
internal fun SliverLayout(
    key: Any,
    measureScope: SliverMeasureScope,
    constraints: SliverConstraints,
    children: @Composable() () -> Unit,
    onResult: (SliverGeometry) -> Unit,
    measureBlock: SliverMeasureBlock
) = composable(key) {
    Layout(children = children) { _, _ ->
        measureScope.layoutNode = (this as LayoutNode.InnerMeasureScope).layoutNode
        val result = measureBlock(measureScope, constraints)
        onResult(result.geometry)
        val width: IntPx
        val height: IntPx

        when (constraints.mainAxisDirection) {
            Direction.LEFT, Direction.RIGHT -> {
                width = constraints.viewportMainAxisSpace.round()
                height = constraints.viewportCrossAxisSpace.round()
            }
            Direction.DOWN, Direction.UP -> {
                width = constraints.viewportCrossAxisSpace.round()
                height = constraints.viewportMainAxisSpace.round()
            }
        }

        layout(width = width, height = height, placementBlock = result.placementBlock)
    }
}