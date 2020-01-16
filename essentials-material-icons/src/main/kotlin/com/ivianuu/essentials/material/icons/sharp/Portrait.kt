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

val Icons.Sharp.Portrait: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 12.25f)
        curveToRelative(1.24f, 0.0f, 2.25f, -1.01f, 2.25f, -2.25f)
        reflectiveCurveTo(13.24f, 7.75f, 12.0f, 7.75f)
        reflectiveCurveTo(9.75f, 8.76f, 9.75f, 10.0f)
        reflectiveCurveToRelative(1.01f, 2.25f, 2.25f, 2.25f)
        close()
        moveTo(16.5f, 16.25f)
        curveToRelative(0.0f, -1.5f, -3.0f, -2.25f, -4.5f, -2.25f)
        reflectiveCurveToRelative(-4.5f, 0.75f, -4.5f, 2.25f)
        lineTo(7.5f, 17.0f)
        horizontalLineToRelative(9.0f)
        verticalLineToRelative(-0.75f)
        close()
        moveTo(21.0f, 3.0f)
        lineTo(3.0f, 3.0f)
        verticalLineToRelative(18.0f)
        horizontalLineToRelative(18.0f)
        lineTo(21.0f, 3.0f)
        close()
        moveTo(19.0f, 19.0f)
        lineTo(5.0f, 19.0f)
        lineTo(5.0f, 5.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(14.0f)
        close()
    }
}
