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
import androidx.ui.core.Px
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollDirection

class SliverChildren {

    internal val children = mutableListOf<SliverChild>()

    fun Sliver(key: Any? = null, measureBlock: SliverMeasureBlock) {
        children += SliverChild(key, measureBlock)
    }

}

internal data class SliverChild(
    val key: Any?,
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
    val crossAxisSpace: Px,
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

class SliverMeasureScope(override val density: Density) : DensityScope {
    fun content(
        geometry: SliverGeometry,
        child: @Composable() () -> Unit
    ): SliverMeasureResult = SliverMeasureResult(geometry, child)
}

typealias SliverMeasureBlock = SliverMeasureScope.(constraints: SliverConstraints) -> SliverMeasureResult

data class SliverMeasureResult internal constructor(
    val geometry: SliverGeometry,
    val child: @Composable() () -> Unit
)