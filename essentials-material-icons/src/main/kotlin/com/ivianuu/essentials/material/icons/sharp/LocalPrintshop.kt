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

val Icons.Sharp.LocalPrintshop: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(2.0f, 8.0f)
        verticalLineToRelative(9.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(-4.0f)
        horizontalLineToRelative(4.0f)
        lineTo(22.0f, 8.0f)
        lineTo(2.0f, 8.0f)
        close()
        moveTo(16.0f, 19.0f)
        lineTo(8.0f, 19.0f)
        verticalLineToRelative(-5.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(5.0f)
        close()
        moveTo(19.0f, 12.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        reflectiveCurveToRelative(0.45f, -1.0f, 1.0f, -1.0f)
        reflectiveCurveToRelative(1.0f, 0.45f, 1.0f, 1.0f)
        reflectiveCurveToRelative(-0.45f, 1.0f, -1.0f, 1.0f)
        close()
        moveTo(18.0f, 3.0f)
        lineTo(6.0f, 3.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(12.0f)
        lineTo(18.0f, 3.0f)
        close()
    }
}
