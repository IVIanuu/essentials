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

package com.ivianuu.essentials.material.icons.rounded

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.PersonPin: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(19.0f, 2.0f)
        lineTo(5.0f, 2.0f)
        curveToRelative(-1.11f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(4.0f)
        lineToRelative(2.29f, 2.29f)
        curveToRelative(0.39f, 0.39f, 1.02f, 0.39f, 1.41f, 0.0f)
        lineTo(15.0f, 20.0f)
        horizontalLineToRelative(4.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(21.0f, 4.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(12.0f, 5.3f)
        curveToRelative(1.49f, 0.0f, 2.7f, 1.21f, 2.7f, 2.7f)
        reflectiveCurveToRelative(-1.21f, 2.7f, -2.7f, 2.7f)
        reflectiveCurveTo(9.3f, 9.49f, 9.3f, 8.0f)
        reflectiveCurveToRelative(1.21f, -2.7f, 2.7f, -2.7f)
        close()
        moveTo(18.0f, 16.0f)
        lineTo(6.0f, 16.0f)
        verticalLineToRelative(-0.9f)
        curveToRelative(0.0f, -2.0f, 4.0f, -3.1f, 6.0f, -3.1f)
        reflectiveCurveToRelative(6.0f, 1.1f, 6.0f, 3.1f)
        verticalLineToRelative(0.9f)
        close()
    }
}
