/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.material.icons

import androidx.ui.graphics.Color
import androidx.ui.graphics.SolidColor
import androidx.ui.graphics.StrokeCap
import androidx.ui.graphics.StrokeJoin
import androidx.ui.graphics.vector.PathBuilder
import androidx.ui.graphics.vector.PathData
import androidx.ui.graphics.vector.VectorAssetBuilder
import androidx.ui.unit.dp

/**
 * Entry point for using [Material Icons](https://material.io/resources/icons).
 *
 * There are five distinct icon themes: [Filled], [Outlined], [Rounded], [TwoTone], and [Sharp].
 * Each theme contains the same icons, but with a distinct visual style. You should typically
 * choose one theme and use it across your application for consistency.
 *
 * Icons maintain the same names defined by Material, but with their snake_case name converted to
 * PascalCase. For example: add_alarm becomes AddAlarm.
 *
 * Note: Icons that start with a number, such as `360`, are prefixed with a '_', becoming '_360'.
 *
 * Icons are represented as [androidx.ui.graphics.vector.VectorAsset]s and hence can be drawn by
 * simply using [androidx.ui.graphics.vector.DrawVector].
 */
object Icons {
    object Filled
    object Outlined
    object Rounded
    object TwoTone
    object Sharp

    /**
     * Alias for [Filled], the baseline icon theme.
     */
    val Default = Filled
}

/**
 * Utility delegate to construct a lazily initialized Material icon with default size information.
 *
 * @param width width for this icon. This is used both for the container width (this value in [dp])
 * and also for the viewport width.
 * @param height height for this icon. This is used both for the container width
 * (this value in [dp]) and also for the viewport width.
 * @param block builder lambda to add paths to this vector asset
 */
fun lazyMaterialIcon(
    width: Float = 24f,
    height: Float = 24f,
    block: VectorAssetBuilder.() -> VectorAssetBuilder
) = lazy {
    VectorAssetBuilder(
        defaultWidth = width.dp,
        defaultHeight = height.dp,
        viewportWidth = width,
        viewportHeight = height
    ).block().build()
}

/**
 * Utility function to add a vector path with sane Material defaults.
 *
 * @param fillAlpha fill alpha for this path
 * @param strokeAlpha stroke alpha for this path
 * @param block builder lambda to add commands to this path
 */
fun VectorAssetBuilder.path(
    fillAlpha: Float = 1f,
    strokeAlpha: Float = 1f,
    block: PathBuilder.() -> Unit
) = this.apply {
    // TODO: b/146213225
    // Some of these defaults are already set when parsing from XML, but do not currently exist when
    // added programmatically. We should unify these and simplify the following where possible.
    addPath(
        PathData(block),
        fill = SolidColor(Color.Black),
        fillAlpha = fillAlpha,
        stroke = null,
        strokeAlpha = strokeAlpha,
        strokeLineWidth = 1f,
        strokeLineCap = StrokeCap.butt,
        strokeLineJoin = StrokeJoin.bevel,
        strokeLineMiter = 1f
    )
}

/**
 * Utility function to add a vector group for Material icons
 *
 * @param block builder lambda to add paths to this group
 */
fun VectorAssetBuilder.group(block: VectorAssetBuilder.() -> Unit) = this.apply {
    pushGroup()
    block()
    popGroup()
}
