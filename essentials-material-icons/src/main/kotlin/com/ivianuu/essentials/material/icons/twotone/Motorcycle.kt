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

val Icons.TwoTone.Motorcycle: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(9.7f, 12.31f)
        lineToRelative(0.25f, 0.69f)
        horizontalLineToRelative(0.77f)
        lineToRelative(2.0f, -2.0f)
        horizontalLineTo(8.98f)
        curveToRelative(0.3f, 0.39f, 0.54f, 0.83f, 0.72f, 1.31f)
        close()
    }
    path {
        moveTo(19.44f, 9.03f)
        lineTo(15.41f, 5.0f)
        lineTo(11.0f, 5.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(3.59f)
        lineToRelative(2.0f, 2.0f)
        lineTo(5.0f, 9.0f)
        curveToRelative(-2.8f, 0.0f, -5.0f, 2.2f, -5.0f, 5.0f)
        reflectiveCurveToRelative(2.2f, 5.0f, 5.0f, 5.0f)
        curveToRelative(2.46f, 0.0f, 4.45f, -1.69f, 4.9f, -4.0f)
        horizontalLineToRelative(1.65f)
        lineToRelative(2.77f, -2.77f)
        curveToRelative(-0.21f, 0.54f, -0.32f, 1.14f, -0.32f, 1.77f)
        curveToRelative(0.0f, 2.8f, 2.2f, 5.0f, 5.0f, 5.0f)
        reflectiveCurveToRelative(5.0f, -2.2f, 5.0f, -5.0f)
        curveToRelative(0.0f, -2.65f, -1.97f, -4.77f, -4.56f, -4.97f)
        close()
        moveTo(7.82f, 15.0f)
        curveTo(7.4f, 16.15f, 6.28f, 17.0f, 5.0f, 17.0f)
        curveToRelative(-1.63f, 0.0f, -3.0f, -1.37f, -3.0f, -3.0f)
        reflectiveCurveToRelative(1.37f, -3.0f, 3.0f, -3.0f)
        curveToRelative(1.28f, 0.0f, 2.4f, 0.85f, 2.82f, 2.0f)
        lineTo(5.0f, 13.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(2.82f)
        close()
        moveTo(10.72f, 13.0f)
        horizontalLineToRelative(-0.77f)
        lineToRelative(-0.25f, -0.69f)
        curveToRelative(-0.18f, -0.48f, -0.42f, -0.92f, -0.72f, -1.31f)
        horizontalLineToRelative(3.74f)
        lineToRelative(-2.0f, 2.0f)
        close()
        moveTo(19.0f, 17.0f)
        curveToRelative(-1.66f, 0.0f, -3.0f, -1.34f, -3.0f, -3.0f)
        reflectiveCurveToRelative(1.34f, -3.0f, 3.0f, -3.0f)
        reflectiveCurveToRelative(3.0f, 1.34f, 3.0f, 3.0f)
        reflectiveCurveToRelative(-1.34f, 3.0f, -3.0f, 3.0f)
        close()
    }
}
