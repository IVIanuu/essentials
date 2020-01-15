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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.SwapHorizontalCircle: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(22.0f, 12.0f)
        curveToRelative(0.0f, -5.52f, -4.48f, -10.0f, -10.0f, -10.0f)
        reflectiveCurveTo(2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
        close()
        moveTo(15.0f, 6.5f)
        lineToRelative(3.5f, 3.5f)
        lineToRelative(-3.5f, 3.5f)
        lineTo(15.0f, 11.0f)
        horizontalLineToRelative(-4.0f)
        lineTo(11.0f, 9.0f)
        horizontalLineToRelative(4.0f)
        lineTo(15.0f, 6.5f)
        close()
        moveTo(9.0f, 17.5f)
        lineTo(5.5f, 14.0f)
        lineTo(9.0f, 10.5f)
        lineTo(9.0f, 13.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(2.0f)
        lineTo(9.0f, 15.0f)
        verticalLineToRelative(2.5f)
        close()
    }
}
