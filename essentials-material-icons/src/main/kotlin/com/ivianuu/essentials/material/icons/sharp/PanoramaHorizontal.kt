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

val Icons.Sharp.PanoramaHorizontal: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(4.0f, 6.55f)
        curveToRelative(2.6f, 0.77f, 5.28f, 1.16f, 8.0f, 1.16f)
        curveToRelative(2.72f, 0.0f, 5.41f, -0.39f, 8.0f, -1.16f)
        verticalLineToRelative(10.91f)
        curveToRelative(-2.6f, -0.77f, -5.28f, -1.16f, -8.0f, -1.16f)
        curveToRelative(-2.72f, 0.0f, -5.41f, 0.39f, -8.0f, 1.16f)
        verticalLineTo(6.55f)
        moveTo(2.0f, 3.77f)
        verticalLineToRelative(16.47f)
        reflectiveCurveToRelative(0.77f, -0.26f, 0.88f, -0.3f)
        curveTo(5.82f, 18.85f, 8.91f, 18.3f, 12.0f, 18.3f)
        curveToRelative(3.09f, 0.0f, 6.18f, 0.55f, 9.12f, 1.64f)
        curveToRelative(0.11f, 0.04f, 0.88f, 0.3f, 0.88f, 0.3f)
        verticalLineTo(3.77f)
        reflectiveCurveToRelative(-0.77f, 0.26f, -0.88f, 0.3f)
        curveTo(18.18f, 5.15f, 15.09f, 5.71f, 12.0f, 5.71f)
        reflectiveCurveToRelative(-6.18f, -0.56f, -9.12f, -1.64f)
        curveToRelative(-0.11f, -0.05f, -0.88f, -0.3f, -0.88f, -0.3f)
        close()
    }
}
