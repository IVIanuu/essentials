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

val Icons.TwoTone.Person: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 16.0f)
        curveToRelative(-2.69f, 0.0f, -5.77f, 1.28f, -6.0f, 2.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(-0.2f, -0.71f, -3.3f, -2.0f, -6.0f, -2.0f)
        close()
    }
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 8.0f)
        moveToRelative(-2.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
    }
    path {
        moveTo(12.0f, 14.0f)
        curveToRelative(-2.67f, 0.0f, -8.0f, 1.34f, -8.0f, 4.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(-2.0f)
        curveToRelative(0.0f, -2.66f, -5.33f, -4.0f, -8.0f, -4.0f)
        close()
        moveTo(6.0f, 18.0f)
        curveToRelative(0.22f, -0.72f, 3.31f, -2.0f, 6.0f, -2.0f)
        curveToRelative(2.7f, 0.0f, 5.8f, 1.29f, 6.0f, 2.0f)
        lineTo(6.0f, 18.0f)
        close()
        moveTo(12.0f, 12.0f)
        curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
        reflectiveCurveToRelative(-1.79f, -4.0f, -4.0f, -4.0f)
        reflectiveCurveToRelative(-4.0f, 1.79f, -4.0f, 4.0f)
        reflectiveCurveToRelative(1.79f, 4.0f, 4.0f, 4.0f)
        close()
        moveTo(12.0f, 6.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
        reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
        reflectiveCurveToRelative(-2.0f, -0.9f, -2.0f, -2.0f)
        reflectiveCurveToRelative(0.9f, -2.0f, 2.0f, -2.0f)
        close()
    }
}
