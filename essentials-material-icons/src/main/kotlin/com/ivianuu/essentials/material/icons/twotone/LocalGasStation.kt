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

val Icons.TwoTone.LocalGasStation: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(6.0f, 19.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(-7.0f)
        horizontalLineTo(6.0f)
        close()
    }
    path {
        moveTo(12.0f, 3.0f)
        lineTo(6.0f, 3.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(16.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(-7.5f)
        horizontalLineToRelative(1.5f)
        verticalLineToRelative(5.0f)
        curveToRelative(0.0f, 1.38f, 1.12f, 2.5f, 2.5f, 2.5f)
        reflectiveCurveToRelative(2.5f, -1.12f, 2.5f, -2.5f)
        lineTo(20.5f, 9.0f)
        curveToRelative(0.0f, -0.69f, -0.28f, -1.32f, -0.73f, -1.77f)
        lineToRelative(0.01f, -0.01f)
        lineToRelative(-3.72f, -3.72f)
        lineTo(15.0f, 4.56f)
        lineToRelative(2.11f, 2.11f)
        curveToRelative(-0.94f, 0.36f, -1.61f, 1.26f, -1.61f, 2.33f)
        curveToRelative(0.0f, 1.38f, 1.12f, 2.5f, 2.5f, 2.5f)
        curveToRelative(0.36f, 0.0f, 0.69f, -0.08f, 1.0f, -0.21f)
        verticalLineToRelative(7.21f)
        curveToRelative(0.0f, 0.55f, -0.45f, 1.0f, -1.0f, 1.0f)
        reflectiveCurveToRelative(-1.0f, -0.45f, -1.0f, -1.0f)
        lineTo(17.0f, 14.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        horizontalLineToRelative(-1.0f)
        lineTo(14.0f, 5.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(12.0f, 13.5f)
        lineTo(12.0f, 19.0f)
        lineTo(6.0f, 19.0f)
        verticalLineToRelative(-7.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(1.5f)
        close()
        moveTo(12.0f, 10.0f)
        lineTo(6.0f, 10.0f)
        lineTo(6.0f, 5.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(5.0f)
        close()
        moveTo(18.0f, 10.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        reflectiveCurveToRelative(0.45f, -1.0f, 1.0f, -1.0f)
        reflectiveCurveToRelative(1.0f, 0.45f, 1.0f, 1.0f)
        reflectiveCurveToRelative(-0.45f, 1.0f, -1.0f, 1.0f)
        close()
    }
}
