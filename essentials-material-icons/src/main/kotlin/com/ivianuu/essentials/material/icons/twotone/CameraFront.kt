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

val Icons.TwoTone.CameraFront: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(7.0f, 14.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(2.0f)
        horizontalLineTo(7.0f)
        close()
    }
    path {
        moveTo(5.0f, 20.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(2.0f)
        lineToRelative(3.0f, -3.0f)
        lineToRelative(-3.0f, -3.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(14.0f, 20.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-5.0f)
        close()
        moveTo(11.99f, 8.0f)
        curveTo(13.1f, 8.0f, 14.0f, 7.1f, 14.0f, 6.0f)
        reflectiveCurveToRelative(-0.9f, -2.0f, -2.01f, -2.0f)
        reflectiveCurveTo(10.0f, 4.9f, 10.0f, 6.0f)
        reflectiveCurveToRelative(0.89f, 2.0f, 1.99f, 2.0f)
        close()
        moveTo(17.0f, 0.0f)
        lineTo(7.0f, 0.0f)
        curveTo(5.9f, 0.0f, 5.0f, 0.9f, 5.0f, 2.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(19.0f, 2.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(17.0f, 16.0f)
        lineTo(7.0f, 16.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(17.0f, 12.5f)
        curveToRelative(0.0f, -1.67f, -3.33f, -2.5f, -5.0f, -2.5f)
        reflectiveCurveToRelative(-5.0f, 0.83f, -5.0f, 2.5f)
        lineTo(7.0f, 2.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(10.5f)
        close()
    }
}
