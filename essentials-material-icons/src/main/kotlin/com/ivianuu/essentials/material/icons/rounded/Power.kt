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

package com.ivianuu.essentials.material.icons.rounded

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.Power: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(16.01f, 7.0f)
        lineTo(16.0f, 4.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        reflectiveCurveToRelative(-1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineTo(4.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        reflectiveCurveToRelative(-1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(-0.01f)
        curveTo(6.9f, 7.0f, 6.0f, 7.9f, 6.0f, 8.99f)
        verticalLineToRelative(4.66f)
        curveToRelative(0.0f, 0.53f, 0.21f, 1.04f, 0.58f, 1.41f)
        lineTo(9.5f, 18.0f)
        verticalLineToRelative(2.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(3.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-2.0f)
        lineToRelative(2.92f, -2.92f)
        curveToRelative(0.37f, -0.38f, 0.58f, -0.89f, 0.58f, -1.42f)
        verticalLineTo(8.99f)
        curveTo(18.0f, 7.89f, 17.11f, 7.0f, 16.01f, 7.0f)
        close()
    }
}
