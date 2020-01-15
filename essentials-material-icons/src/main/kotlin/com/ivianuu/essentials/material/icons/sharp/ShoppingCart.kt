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

package com.ivianuu.essentials.material.icons.sharp

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Sharp.ShoppingCart: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(17.0f, 18.0f)
        curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
        reflectiveCurveToRelative(0.89f, 2.0f, 1.99f, 2.0f)
        reflectiveCurveToRelative(2.0f, -0.9f, 2.0f, -2.0f)
        reflectiveCurveToRelative(-0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(7.0f, 18.0f)
        curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
        reflectiveCurveTo(5.9f, 22.0f, 7.0f, 22.0f)
        reflectiveCurveToRelative(2.0f, -0.9f, 2.0f, -2.0f)
        reflectiveCurveToRelative(-0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(7.0f, 15.0f)
        lineToRelative(1.1f, -2.0f)
        horizontalLineToRelative(7.45f)
        curveToRelative(0.75f, 0.0f, 1.41f, -0.41f, 1.75f, -1.03f)
        lineTo(21.7f, 4.0f)
        lineTo(5.21f, 4.0f)
        lineToRelative(-0.94f, -2.0f)
        lineTo(1.0f, 2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(2.0f)
        lineToRelative(3.6f, 7.59f)
        lineTo(3.62f, 17.0f)
        lineTo(19.0f, 17.0f)
        verticalLineToRelative(-2.0f)
        lineTo(7.0f, 15.0f)
        close()
    }
}
