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

val Icons.Sharp.RestorePage: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(14.0f, 2.0f)
        lineTo(4.0f, 2.0f)
        verticalLineToRelative(20.0f)
        horizontalLineToRelative(16.0f)
        lineTo(20.0f, 8.0f)
        lineToRelative(-6.0f, -6.0f)
        close()
        moveTo(12.0f, 18.0f)
        curveToRelative(-2.05f, 0.0f, -3.81f, -1.24f, -4.58f, -3.0f)
        horizontalLineToRelative(1.71f)
        curveToRelative(0.63f, 0.9f, 1.68f, 1.5f, 2.87f, 1.5f)
        curveToRelative(1.93f, 0.0f, 3.5f, -1.57f, 3.5f, -3.5f)
        reflectiveCurveTo(13.93f, 9.5f, 12.0f, 9.5f)
        curveToRelative(-1.35f, 0.0f, -2.52f, 0.78f, -3.1f, 1.9f)
        lineToRelative(1.6f, 1.6f)
        horizontalLineToRelative(-4.0f)
        lineTo(6.5f, 9.0f)
        lineToRelative(1.3f, 1.3f)
        curveTo(8.69f, 8.92f, 10.23f, 8.0f, 12.0f, 8.0f)
        curveToRelative(2.76f, 0.0f, 5.0f, 2.24f, 5.0f, 5.0f)
        reflectiveCurveToRelative(-2.24f, 5.0f, -5.0f, 5.0f)
        close()
    }
}
