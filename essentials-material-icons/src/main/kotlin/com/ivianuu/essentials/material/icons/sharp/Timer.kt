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

val Icons.Sharp.Timer: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(15.0f, 1.0f)
        lineTo(9.0f, 1.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(6.0f)
        lineTo(15.0f, 1.0f)
        close()
        moveTo(11.0f, 14.0f)
        horizontalLineToRelative(2.0f)
        lineTo(13.0f, 8.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(6.0f)
        close()
        moveTo(19.03f, 7.39f)
        lineToRelative(1.42f, -1.42f)
        curveToRelative(-0.43f, -0.51f, -0.9f, -0.99f, -1.41f, -1.41f)
        lineToRelative(-1.42f, 1.42f)
        curveTo(16.07f, 4.74f, 14.12f, 4.0f, 12.0f, 4.0f)
        curveToRelative(-4.97f, 0.0f, -9.0f, 4.03f, -9.0f, 9.0f)
        reflectiveCurveToRelative(4.02f, 9.0f, 9.0f, 9.0f)
        reflectiveCurveToRelative(9.0f, -4.03f, 9.0f, -9.0f)
        curveToRelative(0.0f, -2.12f, -0.74f, -4.07f, -1.97f, -5.61f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-3.87f, 0.0f, -7.0f, -3.13f, -7.0f, -7.0f)
        reflectiveCurveToRelative(3.13f, -7.0f, 7.0f, -7.0f)
        reflectiveCurveToRelative(7.0f, 3.13f, 7.0f, 7.0f)
        reflectiveCurveToRelative(-3.13f, 7.0f, -7.0f, 7.0f)
        close()
    }
}
