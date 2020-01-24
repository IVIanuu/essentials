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

val Icons.Sharp.ContactPhone: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(23.99f, 3.0f)
        lineTo(0.0f, 3.0f)
        verticalLineToRelative(18.0f)
        horizontalLineToRelative(24.0f)
        lineToRelative(-0.01f, -18.0f)
        close()
        moveTo(8.0f, 6.0f)
        curveToRelative(1.66f, 0.0f, 3.0f, 1.34f, 3.0f, 3.0f)
        reflectiveCurveToRelative(-1.34f, 3.0f, -3.0f, 3.0f)
        reflectiveCurveToRelative(-3.0f, -1.34f, -3.0f, -3.0f)
        reflectiveCurveToRelative(1.34f, -3.0f, 3.0f, -3.0f)
        close()
        moveTo(14.0f, 18.0f)
        lineTo(2.0f, 18.0f)
        verticalLineToRelative(-1.0f)
        curveToRelative(0.0f, -2.0f, 4.0f, -3.1f, 6.0f, -3.1f)
        reflectiveCurveToRelative(6.0f, 1.1f, 6.0f, 3.1f)
        verticalLineToRelative(1.0f)
        close()
        moveTo(17.85f, 14.0f)
        horizontalLineToRelative(1.64f)
        lineTo(21.0f, 16.0f)
        lineToRelative(-1.99f, 1.99f)
        curveToRelative(-1.31f, -0.98f, -2.28f, -2.38f, -2.73f, -3.99f)
        curveToRelative(-0.18f, -0.64f, -0.28f, -1.31f, -0.28f, -2.0f)
        reflectiveCurveToRelative(0.1f, -1.36f, 0.28f, -2.0f)
        curveToRelative(0.45f, -1.62f, 1.42f, -3.01f, 2.73f, -3.99f)
        lineTo(21.0f, 8.0f)
        lineToRelative(-1.51f, 2.0f)
        horizontalLineToRelative(-1.64f)
        curveToRelative(-0.22f, 0.63f, -0.35f, 1.3f, -0.35f, 2.0f)
        reflectiveCurveToRelative(0.13f, 1.37f, 0.35f, 2.0f)
        close()
    }
}
