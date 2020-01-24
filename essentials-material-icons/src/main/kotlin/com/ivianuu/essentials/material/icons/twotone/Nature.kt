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

val Icons.TwoTone.Nature: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.17f, 4.17f)
        curveToRelative(-2.76f, 0.0f, -5.0f, 2.24f, -5.0f, 5.0f)
        reflectiveCurveToRelative(2.24f, 5.0f, 5.0f, 5.0f)
        reflectiveCurveToRelative(5.0f, -2.24f, 5.0f, -5.0f)
        reflectiveCurveToRelative(-2.25f, -5.0f, -5.0f, -5.0f)
        close()
    }
    path {
        moveTo(19.17f, 9.17f)
        curveToRelative(0.0f, -3.87f, -3.13f, -7.0f, -7.0f, -7.0f)
        reflectiveCurveToRelative(-7.0f, 3.13f, -7.0f, 7.0f)
        curveToRelative(0.0f, 3.47f, 2.52f, 6.34f, 5.83f, 6.89f)
        lineTo(11.0f, 20.0f)
        lineTo(5.0f, 20.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(-6.0f)
        verticalLineToRelative(-3.88f)
        horizontalLineToRelative(-0.03f)
        curveToRelative(3.49f, -0.4f, 6.2f, -3.36f, 6.2f, -6.95f)
        close()
        moveTo(12.17f, 14.17f)
        curveToRelative(-2.76f, 0.0f, -5.0f, -2.24f, -5.0f, -5.0f)
        reflectiveCurveToRelative(2.24f, -5.0f, 5.0f, -5.0f)
        reflectiveCurveToRelative(5.0f, 2.24f, 5.0f, 5.0f)
        reflectiveCurveToRelative(-2.25f, 5.0f, -5.0f, 5.0f)
        close()
    }
}
