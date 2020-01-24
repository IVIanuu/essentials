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

val Icons.TwoTone.FiberSmartRecord: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(9.0f, 18.0f)
        curveToRelative(3.31f, 0.0f, 6.0f, -2.69f, 6.0f, -6.0f)
        reflectiveCurveToRelative(-2.69f, -6.0f, -6.0f, -6.0f)
        reflectiveCurveToRelative(-6.0f, 2.69f, -6.0f, 6.0f)
        reflectiveCurveToRelative(2.69f, 6.0f, 6.0f, 6.0f)
        close()
    }
    path {
        moveTo(9.0f, 20.0f)
        curveToRelative(4.42f, 0.0f, 8.0f, -3.58f, 8.0f, -8.0f)
        reflectiveCurveToRelative(-3.58f, -8.0f, -8.0f, -8.0f)
        reflectiveCurveToRelative(-8.0f, 3.58f, -8.0f, 8.0f)
        reflectiveCurveToRelative(3.58f, 8.0f, 8.0f, 8.0f)
        close()
        moveTo(9.0f, 6.0f)
        curveToRelative(3.31f, 0.0f, 6.0f, 2.69f, 6.0f, 6.0f)
        reflectiveCurveToRelative(-2.69f, 6.0f, -6.0f, 6.0f)
        reflectiveCurveToRelative(-6.0f, -2.69f, -6.0f, -6.0f)
        reflectiveCurveToRelative(2.69f, -6.0f, 6.0f, -6.0f)
        close()
        moveTo(17.0f, 4.26f)
        verticalLineToRelative(2.09f)
        curveToRelative(2.33f, 0.82f, 4.0f, 3.04f, 4.0f, 5.65f)
        reflectiveCurveToRelative(-1.67f, 4.83f, -4.0f, 5.65f)
        verticalLineToRelative(2.09f)
        curveToRelative(3.45f, -0.89f, 6.0f, -4.01f, 6.0f, -7.74f)
        curveToRelative(0.0f, -3.73f, -2.55f, -6.85f, -6.0f, -7.74f)
        close()
    }
}
