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

val Icons.TwoTone.HowToReg: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(11.0f, 8.0f)
        moveToRelative(-2.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
    }
    path(fillAlpha = 0.3f) {
        moveTo(5.0f, 18.0f)
        horizontalLineToRelative(4.99f)
        lineTo(9.0f, 17.0f)
        lineToRelative(0.93f, -0.94f)
        curveTo(7.55f, 16.33f, 5.2f, 17.37f, 5.0f, 18.0f)
        close()
    }
    path {
        moveTo(11.0f, 12.0f)
        curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
        reflectiveCurveToRelative(-1.79f, -4.0f, -4.0f, -4.0f)
        reflectiveCurveToRelative(-4.0f, 1.79f, -4.0f, 4.0f)
        reflectiveCurveToRelative(1.79f, 4.0f, 4.0f, 4.0f)
        close()
        moveTo(11.0f, 6.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
        reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
        reflectiveCurveToRelative(-2.0f, -0.9f, -2.0f, -2.0f)
        reflectiveCurveToRelative(0.9f, -2.0f, 2.0f, -2.0f)
        close()
        moveTo(10.0f, 18.0f)
        lineTo(5.0f, 18.0f)
        curveToRelative(0.2f, -0.63f, 2.55f, -1.67f, 4.93f, -1.94f)
        horizontalLineToRelative(0.03f)
        lineToRelative(0.46f, -0.45f)
        lineTo(12.0f, 14.06f)
        curveToRelative(-0.39f, -0.04f, -0.68f, -0.06f, -1.0f, -0.06f)
        curveToRelative(-2.67f, 0.0f, -8.0f, 1.34f, -8.0f, 4.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(9.0f)
        lineToRelative(-2.0f, -2.0f)
        close()
        moveTo(20.6f, 12.5f)
        lineToRelative(-5.13f, 5.17f)
        lineToRelative(-2.07f, -2.08f)
        lineTo(12.0f, 17.0f)
        lineToRelative(3.47f, 3.5f)
        lineTo(22.0f, 13.91f)
        close()
    }
}
