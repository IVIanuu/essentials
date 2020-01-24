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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.Brightness2: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(10.0f, 2.0f)
        curveToRelative(-1.82f, 0.0f, -3.53f, 0.5f, -5.0f, 1.35f)
        curveTo(7.99f, 5.08f, 10.0f, 8.3f, 10.0f, 12.0f)
        reflectiveCurveToRelative(-2.01f, 6.92f, -5.0f, 8.65f)
        curveTo(6.47f, 21.5f, 8.18f, 22.0f, 10.0f, 22.0f)
        curveToRelative(5.52f, 0.0f, 10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(15.52f, 2.0f, 10.0f, 2.0f)
        close()
    }
}
