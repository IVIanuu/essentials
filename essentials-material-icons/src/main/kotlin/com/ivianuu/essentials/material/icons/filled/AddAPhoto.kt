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

package com.ivianuu.essentials.material.icons.filled

import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.ui.vector.VectorAsset

val Icons.Filled.AddAPhoto: VectorAsset by lazyMaterialIcon {
    group {
        path {
            moveTo(3.0f, 4.0f)
            lineTo(3.0f, 1.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(3.0f)
            horizontalLineToRelative(3.0f)
            verticalLineToRelative(2.0f)
            lineTo(5.0f, 6.0f)
            verticalLineToRelative(3.0f)
            lineTo(3.0f, 9.0f)
            lineTo(3.0f, 6.0f)
            lineTo(0.0f, 6.0f)
            lineTo(0.0f, 4.0f)
            horizontalLineToRelative(3.0f)
            close()
            moveTo(6.0f, 10.0f)
            lineTo(6.0f, 7.0f)
            horizontalLineToRelative(3.0f)
            lineTo(9.0f, 4.0f)
            horizontalLineToRelative(7.0f)
            lineToRelative(1.83f, 2.0f)
            lineTo(21.0f, 6.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
            verticalLineToRelative(12.0f)
            curveToRelative(0.0f, 1.1f, -0.9f, 2.0f, -2.0f, 2.0f)
            lineTo(5.0f, 22.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, -0.9f, -2.0f, -2.0f)
            lineTo(3.0f, 10.0f)
            horizontalLineToRelative(3.0f)
            close()
            moveTo(13.0f, 19.0f)
            curveToRelative(2.76f, 0.0f, 5.0f, -2.24f, 5.0f, -5.0f)
            reflectiveCurveToRelative(-2.24f, -5.0f, -5.0f, -5.0f)
            reflectiveCurveToRelative(-5.0f, 2.24f, -5.0f, 5.0f)
            reflectiveCurveToRelative(2.24f, 5.0f, 5.0f, 5.0f)
            close()
            moveTo(9.8f, 14.0f)
            curveToRelative(0.0f, 1.77f, 1.43f, 3.2f, 3.2f, 3.2f)
            reflectiveCurveToRelative(3.2f, -1.43f, 3.2f, -3.2f)
            reflectiveCurveToRelative(-1.43f, -3.2f, -3.2f, -3.2f)
            reflectiveCurveToRelative(-3.2f, 1.43f, -3.2f, 3.2f)
            close()
        }
    }
}
