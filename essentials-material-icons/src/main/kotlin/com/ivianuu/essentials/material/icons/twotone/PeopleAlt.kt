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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.PeopleAlt: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f, strokeAlpha = 0.3f) {
        moveTo(9.0f, 8.0f)
        moveToRelative(-2.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
    }
    path(fillAlpha = 0.3f, strokeAlpha = 0.3f) {
        moveTo(9.0f, 15.0f)
        curveToRelative(-2.7f, 0.0f, -5.8f, 1.29f, -6.0f, 2.01f)
        lineTo(3.0f, 18.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(-1.0f)
        curveTo(14.8f, 16.29f, 11.7f, 15.0f, 9.0f, 15.0f)
        close()
    }
    path {
        moveTo(16.67f, 13.13f)
        curveTo(18.04f, 14.06f, 19.0f, 15.32f, 19.0f, 17.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(-3.0f)
        curveTo(23.0f, 14.82f, 19.43f, 13.53f, 16.67f, 13.13f)
        close()
    }
    path {
        moveTo(15.0f, 12.0f)
        curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
        curveToRelative(0.0f, -2.21f, -1.79f, -4.0f, -4.0f, -4.0f)
        curveToRelative(-0.47f, 0.0f, -0.91f, 0.1f, -1.33f, 0.24f)
        curveTo(14.5f, 5.27f, 15.0f, 6.58f, 15.0f, 8.0f)
        reflectiveCurveToRelative(-0.5f, 2.73f, -1.33f, 3.76f)
        curveTo(14.09f, 11.9f, 14.53f, 12.0f, 15.0f, 12.0f)
        close()
    }
    path {
        moveTo(9.0f, 12.0f)
        curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
        curveToRelative(0.0f, -2.21f, -1.79f, -4.0f, -4.0f, -4.0f)
        reflectiveCurveTo(5.0f, 5.79f, 5.0f, 8.0f)
        curveTo(5.0f, 10.21f, 6.79f, 12.0f, 9.0f, 12.0f)
        close()
        moveTo(9.0f, 6.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
        curveToRelative(0.0f, 1.1f, -0.9f, 2.0f, -2.0f, 2.0f)
        reflectiveCurveTo(7.0f, 9.1f, 7.0f, 8.0f)
        curveTo(7.0f, 6.9f, 7.9f, 6.0f, 9.0f, 6.0f)
        close()
    }
    path {
        moveTo(9.0f, 13.0f)
        curveToRelative(-2.67f, 0.0f, -8.0f, 1.34f, -8.0f, 4.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(-3.0f)
        curveTo(17.0f, 14.34f, 11.67f, 13.0f, 9.0f, 13.0f)
        close()
        moveTo(15.0f, 18.0f)
        horizontalLineTo(3.0f)
        lineToRelative(0.0f, -0.99f)
        curveTo(3.2f, 16.29f, 6.3f, 15.0f, 9.0f, 15.0f)
        reflectiveCurveToRelative(5.8f, 1.29f, 6.0f, 2.0f)
        verticalLineTo(18.0f)
        close()
    }
}
