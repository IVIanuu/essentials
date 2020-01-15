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

package com.ivianuu.essentials.material.icons.rounded

import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.ui.vector.VectorAsset

val Icons.Rounded.AddAPhoto: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(3.0f, 8.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineTo(6.0f)
        horizontalLineToRelative(2.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
        horizontalLineTo(5.0f)
        verticalLineTo(2.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        reflectiveCurveToRelative(-1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(2.0f)
        horizontalLineTo(1.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        reflectiveCurveToRelative(0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        close()
    }
    path {
        moveTo(13.0f, 14.0f)
        moveToRelative(-3.0f, 0.0f)
        arcToRelative(3.0f, 3.0f, 0.0f, true, true, 6.0f, 0.0f)
        arcToRelative(3.0f, 3.0f, 0.0f, true, true, -6.0f, 0.0f)
    }
    path {
        moveTo(21.0f, 6.0f)
        horizontalLineToRelative(-3.17f)
        lineToRelative(-1.24f, -1.35f)
        curveToRelative(-0.37f, -0.41f, -0.91f, -0.65f, -1.47f, -0.65f)
        horizontalLineToRelative(-6.4f)
        curveToRelative(0.17f, 0.3f, 0.28f, 0.63f, 0.28f, 1.0f)
        curveToRelative(0.0f, 1.1f, -0.9f, 2.0f, -2.0f, 2.0f)
        lineTo(6.0f, 7.0f)
        verticalLineToRelative(1.0f)
        curveToRelative(0.0f, 1.1f, -0.9f, 2.0f, -2.0f, 2.0f)
        curveToRelative(-0.37f, 0.0f, -0.7f, -0.11f, -1.0f, -0.28f)
        lineTo(3.0f, 20.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(23.0f, 8.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(13.0f, 19.0f)
        curveToRelative(-2.76f, 0.0f, -5.0f, -2.24f, -5.0f, -5.0f)
        reflectiveCurveToRelative(2.24f, -5.0f, 5.0f, -5.0f)
        reflectiveCurveToRelative(5.0f, 2.24f, 5.0f, 5.0f)
        reflectiveCurveToRelative(-2.24f, 5.0f, -5.0f, 5.0f)
        close()
    }
}
