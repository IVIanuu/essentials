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

package com.ivianuu.essentials.material.icons.outlined

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Outlined.LocalPizza: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.0f)
        curveTo(8.43f, 2.0f, 5.23f, 3.54f, 3.01f, 6.0f)
        lineTo(12.0f, 22.0f)
        lineToRelative(8.99f, -16.0f)
        curveTo(18.78f, 3.55f, 15.57f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(12.0f, 17.92f)
        lineTo(5.51f, 6.36f)
        curveTo(7.32f, 4.85f, 9.62f, 4.0f, 12.0f, 4.0f)
        reflectiveCurveToRelative(4.68f, 0.85f, 6.49f, 2.36f)
        lineTo(12.0f, 17.92f)
        close()
        moveTo(9.0f, 5.5f)
        curveToRelative(-0.83f, 0.0f, -1.5f, 0.67f, -1.5f, 1.5f)
        reflectiveCurveTo(8.17f, 8.5f, 9.0f, 8.5f)
        reflectiveCurveToRelative(1.5f, -0.67f, 1.5f, -1.5f)
        reflectiveCurveTo(9.82f, 5.5f, 9.0f, 5.5f)
        close()
        moveTo(10.5f, 13.0f)
        curveToRelative(0.0f, 0.83f, 0.67f, 1.5f, 1.5f, 1.5f)
        curveToRelative(0.82f, 0.0f, 1.5f, -0.67f, 1.5f, -1.5f)
        reflectiveCurveToRelative(-0.68f, -1.5f, -1.5f, -1.5f)
        reflectiveCurveToRelative(-1.5f, 0.67f, -1.5f, 1.5f)
        close()
    }
}
