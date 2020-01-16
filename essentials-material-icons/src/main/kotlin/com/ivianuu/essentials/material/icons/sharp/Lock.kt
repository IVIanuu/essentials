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

val Icons.Sharp.Lock: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.0f, 8.0f)
        horizontalLineToRelative(-3.0f)
        lineTo(17.0f, 6.21f)
        curveToRelative(0.0f, -2.61f, -1.91f, -4.94f, -4.51f, -5.19f)
        curveTo(9.51f, 0.74f, 7.0f, 3.08f, 7.0f, 6.0f)
        verticalLineToRelative(2.0f)
        lineTo(4.0f, 8.0f)
        verticalLineToRelative(14.0f)
        horizontalLineToRelative(16.0f)
        lineTo(20.0f, 8.0f)
        close()
        moveTo(12.0f, 17.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, -0.9f, -2.0f, -2.0f)
        reflectiveCurveToRelative(0.9f, -2.0f, 2.0f, -2.0f)
        reflectiveCurveToRelative(2.0f, 0.9f, 2.0f, 2.0f)
        reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
        close()
        moveTo(9.0f, 8.0f)
        lineTo(9.0f, 6.0f)
        curveToRelative(0.0f, -1.66f, 1.34f, -3.0f, 3.0f, -3.0f)
        reflectiveCurveToRelative(3.0f, 1.34f, 3.0f, 3.0f)
        verticalLineToRelative(2.0f)
        lineTo(9.0f, 8.0f)
        close()
    }
}
