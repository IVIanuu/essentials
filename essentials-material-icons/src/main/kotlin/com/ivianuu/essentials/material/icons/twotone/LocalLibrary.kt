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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.LocalLibrary: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(19.0f, 17.13f)
        verticalLineToRelative(-6.95f)
        curveToRelative(-2.1f, 0.38f, -4.05f, 1.35f, -5.64f, 2.83f)
        lineTo(12.0f, 14.28f)
        lineToRelative(-1.36f, -1.27f)
        curveTo(9.05f, 11.53f, 7.1f, 10.56f, 5.0f, 10.18f)
        verticalLineToRelative(6.95f)
        curveToRelative(2.53f, 0.34f, 4.94f, 1.3f, 7.0f, 2.83f)
        curveToRelative(2.07f, -1.52f, 4.47f, -2.49f, 7.0f, -2.83f)
        close()
    }
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 5.0f)
        moveToRelative(-2.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
    }
    path {
        moveTo(16.0f, 5.0f)
        curveToRelative(0.0f, -2.21f, -1.79f, -4.0f, -4.0f, -4.0f)
        reflectiveCurveTo(8.0f, 2.79f, 8.0f, 5.0f)
        reflectiveCurveToRelative(1.79f, 4.0f, 4.0f, 4.0f)
        reflectiveCurveToRelative(4.0f, -1.79f, 4.0f, -4.0f)
        close()
        moveTo(10.0f, 5.0f)
        curveToRelative(0.0f, -1.1f, 0.9f, -2.0f, 2.0f, -2.0f)
        reflectiveCurveToRelative(2.0f, 0.9f, 2.0f, 2.0f)
        reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
        reflectiveCurveToRelative(-2.0f, -0.9f, -2.0f, -2.0f)
        close()
        moveTo(3.0f, 19.0f)
        curveToRelative(3.48f, 0.0f, 6.64f, 1.35f, 9.0f, 3.55f)
        curveToRelative(2.36f, -2.19f, 5.52f, -3.55f, 9.0f, -3.55f)
        lineTo(21.0f, 8.0f)
        curveToRelative(-3.48f, 0.0f, -6.64f, 1.35f, -9.0f, 3.55f)
        curveTo(9.64f, 9.35f, 6.48f, 8.0f, 3.0f, 8.0f)
        verticalLineToRelative(11.0f)
        close()
        moveTo(5.0f, 10.18f)
        curveToRelative(2.1f, 0.38f, 4.05f, 1.35f, 5.64f, 2.83f)
        lineTo(12.0f, 14.28f)
        lineToRelative(1.36f, -1.27f)
        curveToRelative(1.59f, -1.48f, 3.54f, -2.45f, 5.64f, -2.83f)
        verticalLineToRelative(6.95f)
        curveToRelative(-2.53f, 0.34f, -4.93f, 1.3f, -7.0f, 2.82f)
        curveToRelative(-2.06f, -1.52f, -4.47f, -2.49f, -7.0f, -2.83f)
        verticalLineToRelative(-6.94f)
        close()
    }
}
