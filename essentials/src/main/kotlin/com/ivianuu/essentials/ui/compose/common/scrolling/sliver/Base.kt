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
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Density
import androidx.ui.core.DensityScope
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import androidx.ui.core.Placeable
import androidx.ui.core.Px
import androidx.ui.core.ambientDensity
import androidx.ui.core.px
import androidx.ui.core.round
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollPosition
import com.ivianuu.essentials.ui.compose.core.composable
import kotlin.math.absoluteValue

data class SliverConstraints(
    val scrollOffset: Px,
    val precedingScrollExtent: Px,
    val remainingScrollExtent: Px
)

data class SliverGeometry(
    val scrollExtent: Px
)

@Composable
fun SliverLayout(
    position: ScrollPosition,
    children: SliverChildren.() -> Unit
) = composable("SliverLayout") {
    d { "invoke sliver layout" }
    val sliverChildren = SliverChildren().apply(children).children

    val density = +ambientDensity()
    val measureScope = +memo { SliverMeasureScope(density) }

    var constraints = SliverConstraints(
        scrollOffset = -position.currentOffset,
        precedingScrollExtent = Px.Zero,
        remainingScrollExtent = -position.minOffset.value.absoluteValue.px
    )

    val childrenBlocks = mutableListOf<@Composable() () -> Unit>()

    sliverChildren.forEach { child ->
        val measureResult = child.measureBlock(measureScope, constraints)
        childrenBlocks += {
            ParentData(
                data = SliverParentData(measureResult.geometry),
                children = measureResult.content
            )
        }

        constraints = constraints.copy(
            scrollOffset = constraints.scrollOffset + measureResult.geometry.scrollExtent,
            precedingScrollExtent = constraints.precedingScrollExtent + measureResult.geometry.scrollExtent,
            remainingScrollExtent = if (constraints.remainingScrollExtent == Px.Infinity) Px.Infinity else constraints.remainingScrollExtent - measureResult.geometry.scrollExtent
        )
    }

    SliverViewport(position) {
        d { "invoke sliver view port children" }
        childrenBlocks.forEach { it() }
    }
}

class SliverChildren {

    internal val children = mutableListOf<SliverChild>()

    fun Sliver(measureBlock: SliverMeasureBlock) {
        children += SliverChild(measureBlock)
    }

}

internal data class SliverChild(
    val measureBlock: SliverMeasureBlock
)

class SliverMeasureScope(override val density: Density) : DensityScope {
    fun content(
        scrollExtent: Px,
        content: @Composable() () -> Unit
    ): SliverMeasureResult = SliverMeasureResult(SliverGeometry(scrollExtent), content)
}

typealias SliverMeasureBlock = SliverMeasureScope.(constraints: SliverConstraints) -> SliverMeasureResult

class SliverMeasureResult internal constructor(
    internal val geometry: SliverGeometry,
    internal val content: @Composable() () -> Unit
)

private data class SliverPlaceable(val placeable: Placeable, val geometry: SliverGeometry)
private data class SliverParentData(val geometry: SliverGeometry)

@Composable
private fun SliverViewport(
    position: ScrollPosition,
    children: @Composable() () -> Unit
) = composable("SliverViewport") {
    d { "invoke sliver view port" }
    Layout(children = children) { measureables, constraints ->
        val placeables = measureables.map {
            val parentData = it.parentData as SliverParentData
            val childConstraints = constraints.copy(
                minWidth = constraints.maxWidth, // todo check
                minHeight = parentData.geometry.scrollExtent.round(),
                maxHeight = parentData.geometry.scrollExtent.round()
            )
            SliverPlaceable(it.measure(childConstraints), parentData.geometry)
        }

        d { "measure $constraints" }

        layout(constraints.maxWidth, constraints.maxHeight) {
            var offset = position.currentOffset
            d { "layout $constraints" }
            placeables.forEach { placeable ->
                placeable.placeable.place(0.px, offset)
                offset += placeable.geometry.scrollExtent
            }
        }
    }
}