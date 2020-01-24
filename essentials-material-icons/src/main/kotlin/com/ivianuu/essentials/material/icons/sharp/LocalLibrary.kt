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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Sharp.LocalLibrary: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 11.55f)
        curveTo(9.64f, 9.35f, 6.48f, 8.0f, 3.0f, 8.0f)
        verticalLineToRelative(11.0f)
        curveToRelative(3.48f, 0.0f, 6.64f, 1.35f, 9.0f, 3.55f)
        curveToRelative(2.36f, -2.19f, 5.52f, -3.55f, 9.0f, -3.55f)
        verticalLineTo(8.0f)
        curveToRelative(-3.48f, 0.0f, -6.64f, 1.35f, -9.0f, 3.55f)
        close()
        moveTo(12.0f, 8.0f)
        curveToRelative(1.66f, 0.0f, 3.0f, -1.34f, 3.0f, -3.0f)
        reflectiveCurveToRelative(-1.34f, -3.0f, -3.0f, -3.0f)
        reflectiveCurveToRelative(-3.0f, 1.34f, -3.0f, 3.0f)
        reflectiveCurveToRelative(1.34f, 3.0f, 3.0f, 3.0f)
        close()
    }
}
