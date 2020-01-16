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

package com.ivianuu.essentials.material.icons.twotone

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.TwoTone.Watch: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(14.72f, 4.48f)
        lineTo(14.31f, 2.0f)
        horizontalLineTo(9.7f)
        lineToRelative(-0.41f, 2.47f)
        curveTo(10.13f, 4.17f, 11.05f, 4.0f, 12.0f, 4.0f)
        curveToRelative(0.96f, 0.0f, 1.87f, 0.17f, 2.72f, 0.48f)
        close()
        moveTo(9.29f, 19.53f)
        lineTo(9.7f, 22.0f)
        horizontalLineToRelative(4.61f)
        lineToRelative(0.41f, -2.48f)
        curveToRelative(-0.85f, 0.31f, -1.76f, 0.48f, -2.72f, 0.48f)
        curveToRelative(-0.95f, 0.0f, -1.87f, -0.17f, -2.71f, -0.47f)
        close()
    }
    path {
        moveTo(16.96f, 5.73f)
        lineTo(16.0f, 0.0f)
        lineTo(8.0f, 0.0f)
        lineToRelative(-0.95f, 5.73f)
        curveTo(5.19f, 7.19f, 4.0f, 9.45f, 4.0f, 12.0f)
        reflectiveCurveToRelative(1.19f, 4.81f, 3.05f, 6.27f)
        lineTo(8.0f, 24.0f)
        horizontalLineToRelative(8.0f)
        lineToRelative(0.96f, -5.73f)
        curveTo(18.81f, 16.81f, 20.0f, 14.54f, 20.0f, 12.0f)
        reflectiveCurveToRelative(-1.19f, -4.81f, -3.04f, -6.27f)
        close()
        moveTo(9.7f, 2.0f)
        horizontalLineToRelative(4.61f)
        lineToRelative(0.41f, 2.48f)
        curveTo(13.87f, 4.17f, 12.96f, 4.0f, 12.0f, 4.0f)
        curveToRelative(-0.95f, 0.0f, -1.87f, 0.17f, -2.71f, 0.47f)
        lineTo(9.7f, 2.0f)
        close()
        moveTo(14.31f, 22.0f)
        lineTo(9.7f, 22.0f)
        lineToRelative(-0.41f, -2.47f)
        curveToRelative(0.84f, 0.3f, 1.76f, 0.47f, 2.71f, 0.47f)
        curveToRelative(0.96f, 0.0f, 1.87f, -0.17f, 2.72f, -0.48f)
        lineTo(14.31f, 22.0f)
        close()
        moveTo(12.0f, 18.0f)
        curveToRelative(-3.31f, 0.0f, -6.0f, -2.69f, -6.0f, -6.0f)
        reflectiveCurveToRelative(2.69f, -6.0f, 6.0f, -6.0f)
        reflectiveCurveToRelative(6.0f, 2.69f, 6.0f, 6.0f)
        reflectiveCurveToRelative(-2.69f, 6.0f, -6.0f, 6.0f)
        close()
    }
}
