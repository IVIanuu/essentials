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

val Icons.Rounded.Functions: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(16.5f, 4.0f)
        horizontalLineTo(7.56f)
        curveTo(6.7f, 4.0f, 6.0f, 4.7f, 6.0f, 5.56f)
        curveToRelative(0.0f, 0.28f, 0.12f, 0.55f, 0.32f, 0.74f)
        lineTo(12.5f, 12.0f)
        lineToRelative(-6.18f, 5.7f)
        curveToRelative(-0.2f, 0.19f, -0.32f, 0.46f, -0.32f, 0.74f)
        curveTo(6.0f, 19.3f, 6.7f, 20.0f, 7.56f, 20.0f)
        horizontalLineToRelative(8.94f)
        curveToRelative(0.83f, 0.0f, 1.5f, -0.67f, 1.5f, -1.5f)
        reflectiveCurveToRelative(-0.67f, -1.5f, -1.5f, -1.5f)
        horizontalLineTo(11.0f)
        lineToRelative(3.59f, -3.59f)
        curveToRelative(0.78f, -0.78f, 0.78f, -2.05f, 0.0f, -2.83f)
        lineTo(11.0f, 7.0f)
        horizontalLineToRelative(5.5f)
        curveToRelative(0.83f, 0.0f, 1.5f, -0.67f, 1.5f, -1.5f)
        reflectiveCurveTo(17.33f, 4.0f, 16.5f, 4.0f)
        close()
    }
}
