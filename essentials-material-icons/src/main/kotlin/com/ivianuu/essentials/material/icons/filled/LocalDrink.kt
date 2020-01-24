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

val Icons.Filled.LocalDrink: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(3.0f, 2.0f)
        lineToRelative(2.01f, 18.23f)
        curveTo(5.13f, 21.23f, 5.97f, 22.0f, 7.0f, 22.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(1.03f, 0.0f, 1.87f, -0.77f, 1.99f, -1.77f)
        lineTo(21.0f, 2.0f)
        lineTo(3.0f, 2.0f)
        close()
        moveTo(12.0f, 19.0f)
        curveToRelative(-1.66f, 0.0f, -3.0f, -1.34f, -3.0f, -3.0f)
        curveToRelative(0.0f, -2.0f, 3.0f, -5.4f, 3.0f, -5.4f)
        reflectiveCurveToRelative(3.0f, 3.4f, 3.0f, 5.4f)
        curveToRelative(0.0f, 1.66f, -1.34f, 3.0f, -3.0f, 3.0f)
        close()
        moveTo(18.33f, 8.0f)
        lineTo(5.67f, 8.0f)
        lineToRelative(-0.44f, -4.0f)
        horizontalLineToRelative(13.53f)
        lineToRelative(-0.43f, 4.0f)
        close()
    }
}
