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

val Icons.TwoTone.RemoveRedEye: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 6.5f)
        curveToRelative(-3.79f, 0.0f, -7.17f, 2.13f, -8.82f, 5.5f)
        curveToRelative(1.65f, 3.37f, 5.02f, 5.5f, 8.82f, 5.5f)
        reflectiveCurveToRelative(7.17f, -2.13f, 8.82f, -5.5f)
        curveTo(19.17f, 8.63f, 15.79f, 6.5f, 12.0f, 6.5f)
        close()
        moveTo(12.0f, 16.5f)
        curveToRelative(-2.48f, 0.0f, -4.5f, -2.02f, -4.5f, -4.5f)
        reflectiveCurveTo(9.52f, 7.5f, 12.0f, 7.5f)
        reflectiveCurveToRelative(4.5f, 2.02f, 4.5f, 4.5f)
        reflectiveCurveToRelative(-2.02f, 4.5f, -4.5f, 4.5f)
        close()
    }
    path {
        moveTo(12.0f, 4.5f)
        curveTo(7.0f, 4.5f, 2.73f, 7.61f, 1.0f, 12.0f)
        curveToRelative(1.73f, 4.39f, 6.0f, 7.5f, 11.0f, 7.5f)
        reflectiveCurveToRelative(9.27f, -3.11f, 11.0f, -7.5f)
        curveToRelative(-1.73f, -4.39f, -6.0f, -7.5f, -11.0f, -7.5f)
        close()
        moveTo(12.0f, 17.5f)
        curveToRelative(-3.79f, 0.0f, -7.17f, -2.13f, -8.82f, -5.5f)
        curveTo(4.83f, 8.63f, 8.21f, 6.5f, 12.0f, 6.5f)
        reflectiveCurveToRelative(7.17f, 2.13f, 8.82f, 5.5f)
        curveToRelative(-1.65f, 3.37f, -5.03f, 5.5f, -8.82f, 5.5f)
        close()
        moveTo(12.0f, 7.5f)
        curveToRelative(-2.48f, 0.0f, -4.5f, 2.02f, -4.5f, 4.5f)
        reflectiveCurveToRelative(2.02f, 4.5f, 4.5f, 4.5f)
        reflectiveCurveToRelative(4.5f, -2.02f, 4.5f, -4.5f)
        reflectiveCurveToRelative(-2.02f, -4.5f, -4.5f, -4.5f)
        close()
        moveTo(12.0f, 14.5f)
        curveToRelative(-1.38f, 0.0f, -2.5f, -1.12f, -2.5f, -2.5f)
        reflectiveCurveToRelative(1.12f, -2.5f, 2.5f, -2.5f)
        reflectiveCurveToRelative(2.5f, 1.12f, 2.5f, 2.5f)
        reflectiveCurveToRelative(-1.12f, 2.5f, -2.5f, 2.5f)
        close()
    }
}
