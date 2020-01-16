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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Sharp.LocalMall: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(21.0f, 6.0f)
        horizontalLineToRelative(-4.0f)
        curveToRelative(0.0f, -2.76f, -2.24f, -5.0f, -5.0f, -5.0f)
        reflectiveCurveTo(7.0f, 3.24f, 7.0f, 6.0f)
        lineTo(3.0f, 6.0f)
        verticalLineToRelative(16.0f)
        horizontalLineToRelative(18.0f)
        lineTo(21.0f, 6.0f)
        close()
        moveTo(12.0f, 3.0f)
        curveToRelative(1.66f, 0.0f, 3.0f, 1.34f, 3.0f, 3.0f)
        lineTo(9.0f, 6.0f)
        curveToRelative(0.0f, -1.66f, 1.34f, -3.0f, 3.0f, -3.0f)
        close()
        moveTo(12.0f, 13.0f)
        curveToRelative(-2.76f, 0.0f, -5.0f, -2.24f, -5.0f, -5.0f)
        horizontalLineToRelative(2.0f)
        curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
        reflectiveCurveToRelative(3.0f, -1.34f, 3.0f, -3.0f)
        horizontalLineToRelative(2.0f)
        curveToRelative(0.0f, 2.76f, -2.24f, 5.0f, -5.0f, 5.0f)
        close()
    }
}
