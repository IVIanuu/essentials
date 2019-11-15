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

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.Constraints
import androidx.ui.core.Density
import androidx.ui.core.DensityScope
import androidx.ui.core.Direction
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.Measurable
import androidx.ui.core.Placeable
import androidx.ui.core.Px
import androidx.ui.core.round
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollDirection
import com.ivianuu.essentials.ui.compose.core.composable

class SliverChildren {

    internal val children = mutableListOf<SliverChild>()

    fun Sliver(
        key: Any? = null,
        children: @Composable() () -> Unit,
        measureBlock: SliverMeasureBlock
    ) {
        this.children += SliverChild(
            key = key,
            children = {
                SliverLayout(children = children, measureBlock = measureBlock)
            }
        )
    }

}

internal data class SliverChild(
    val key: Any?,
    val children: @Composable() () -> Unit
)

data class SliverConstraints(
    val mainAxisDirection: Direction,
    val growthDirection: GrowthDirection,
    val userScrollDirection: ScrollDirection,
    val scrollPosition: Px,
    val precedingScrollSpace: Px,
    val overlap: Px,
    val remainingPaintSpace: Px,
    val crossAxisSpace: Px,
    val crossAxisDirection: Direction,
    val viewportMainAxisSpace: Px,
    val remainingCacheSpace: Px,
    val cacheOrigin: Px
)

fun SliverConstraints.asConstraints() = when (mainAxisDirection) {
    Direction.LEFT, Direction.RIGHT -> Constraints(
        minWidth = IntPx.Zero,
        maxWidth = IntPx.Infinity,
        minHeight = crossAxisSpace.round(),
        maxHeight = crossAxisSpace.round()
    )
    Direction.UP, Direction.DOWN -> Constraints(
        minWidth = crossAxisSpace.round(),
        maxWidth = crossAxisSpace.round(),
        minHeight = IntPx.Zero,
        maxHeight = IntPx.Infinity
    )
}

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
) {
    companion object {
        val Zero = SliverGeometry()
    }
}

class SliverMeasureScope(override val density: Density) : DensityScope {
    fun layout(
        geometry: SliverGeometry,
        placementBlock: Placeable.PlacementScope.() -> Unit
    ): SliverLayoutResult = SliverLayoutResult(geometry, placementBlock)
}

typealias SliverMeasureBlock = SliverMeasureScope.(
    measureables: List<Measurable>,
    constraints: SliverConstraints
) -> SliverLayoutResult

data class SliverLayoutResult internal constructor(
    val geometry: SliverGeometry,
    val placementBlock: Placeable.PlacementScope.() -> Unit
)

internal val ViewportStateAmbient = Ambient.of<ViewportState>()

@Composable
fun SliverLayout(
    children: @Composable() () -> Unit,
    measureBlock: SliverMeasureBlock
) = composable("SliverLayout") {
    d { "sliver layout invoke" }
    val state = +ambient(ViewportStateAmbient)
    Layout(children = children) { measureables, _ ->
        d { "sliver layout measureing" }
        val constraints = state.constraints!!
        val measureScope = state.measureScope
        val (geometry, placementBlock) =
            measureBlock(measureScope, measureables, constraints)

        val width: Px
        val height: Px
        when (constraints.mainAxisDirection) {
            Direction.LEFT, Direction.RIGHT -> {
                width = geometry.paintSize // todo
                height = constraints.crossAxisSpace
            }
            Direction.UP, Direction.DOWN -> {
                width = constraints.crossAxisSpace
                height = geometry.paintSize // todo
            }
        }

        state.geometry = geometry

        layout(
            width = width.round(),
            height = height.round()
        ) {
            d { "sliver layout placing" }
            placementBlock()
        }
    }
}