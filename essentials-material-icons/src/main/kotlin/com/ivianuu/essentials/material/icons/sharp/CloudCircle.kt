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

val Icons.Sharp.CloudCircle: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.0f)
        curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(16.5f, 16.0f)
        lineTo(8.0f, 16.0f)
        curveToRelative(-1.66f, 0.0f, -3.0f, -1.34f, -3.0f, -3.0f)
        reflectiveCurveToRelative(1.34f, -3.0f, 3.0f, -3.0f)
        horizontalLineToRelative(0.14f)
        curveToRelative(0.44f, -1.73f, 1.99f, -3.0f, 3.86f, -3.0f)
        curveToRelative(2.21f, 0.0f, 4.0f, 1.79f, 4.0f, 4.0f)
        horizontalLineToRelative(0.5f)
        curveToRelative(1.38f, 0.0f, 2.5f, 1.12f, 2.5f, 2.5f)
        reflectiveCurveTo(17.88f, 16.0f, 16.5f, 16.0f)
        close()
    }
}
