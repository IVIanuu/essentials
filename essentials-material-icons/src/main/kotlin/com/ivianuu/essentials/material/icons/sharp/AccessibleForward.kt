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

import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group

val Icons.Sharp.AccessibleForward: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.0f, 4.54f)
        moveToRelative(-2.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
    }
    path {
        moveTo(15.0f, 17.0f)
        horizontalLineToRelative(-2.0f)
        curveToRelative(0.0f, 1.65f, -1.35f, 3.0f, -3.0f, 3.0f)
        reflectiveCurveToRelative(-3.0f, -1.35f, -3.0f, -3.0f)
        reflectiveCurveToRelative(1.35f, -3.0f, 3.0f, -3.0f)
        verticalLineToRelative(-2.0f)
        curveToRelative(-2.76f, 0.0f, -5.0f, 2.24f, -5.0f, 5.0f)
        reflectiveCurveToRelative(2.24f, 5.0f, 5.0f, 5.0f)
        reflectiveCurveToRelative(5.0f, -2.24f, 5.0f, -5.0f)
        close()
        moveTo(20.0f, 13.5f)
        horizontalLineToRelative(-3.86f)
        lineToRelative(1.67f, -3.67f)
        curveTo(18.42f, 8.5f, 17.44f, 7.0f, 15.96f, 7.0f)
        horizontalLineToRelative(-5.2f)
        curveToRelative(-0.81f, 0.0f, -1.54f, 0.47f, -1.87f, 1.2f)
        lineTo(8.22f, 10.0f)
        lineToRelative(1.92f, 0.53f)
        lineToRelative(0.65f, -1.53f)
        lineTo(13.0f, 9.0f)
        lineToRelative(-3.12f, 7.0f)
        lineTo(18.0f, 16.0f)
        verticalLineToRelative(5.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-7.5f)
        close()
    }
}
