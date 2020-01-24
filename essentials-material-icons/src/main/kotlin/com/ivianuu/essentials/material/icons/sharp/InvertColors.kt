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

val Icons.Sharp.InvertColors: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(17.66f, 7.93f)
        lineTo(12.0f, 2.27f)
        lineTo(6.34f, 7.93f)
        curveToRelative(-3.12f, 3.12f, -3.12f, 8.19f, 0.0f, 11.31f)
        curveTo(7.9f, 20.8f, 9.95f, 21.58f, 12.0f, 21.58f)
        reflectiveCurveToRelative(4.1f, -0.78f, 5.66f, -2.34f)
        curveToRelative(3.12f, -3.12f, 3.12f, -8.19f, 0.0f, -11.31f)
        close()
        moveTo(12.0f, 19.59f)
        curveToRelative(-1.6f, 0.0f, -3.11f, -0.62f, -4.24f, -1.76f)
        curveTo(6.62f, 16.69f, 6.0f, 15.19f, 6.0f, 13.59f)
        reflectiveCurveToRelative(0.62f, -3.11f, 1.76f, -4.24f)
        lineTo(12.0f, 5.1f)
        verticalLineToRelative(14.49f)
        close()
    }
}
