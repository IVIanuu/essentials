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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Filled.AllOut: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(16.21f, 4.16f)
        lineToRelative(4.0f, 4.0f)
        verticalLineToRelative(-4.0f)
        close()
        moveTo(20.21f, 16.16f)
        lineToRelative(-4.0f, 4.0f)
        horizontalLineToRelative(4.0f)
        close()
        moveTo(8.21f, 20.16f)
        lineToRelative(-4.0f, -4.0f)
        verticalLineToRelative(4.0f)
        close()
        moveTo(4.21f, 8.16f)
        lineToRelative(4.0f, -4.0f)
        horizontalLineToRelative(-4.0f)
        close()
        moveTo(17.16f, 7.21f)
        curveToRelative(-2.73f, -2.73f, -7.17f, -2.73f, -9.9f, 0.0f)
        reflectiveCurveToRelative(-2.73f, 7.17f, 0.0f, 9.9f)
        reflectiveCurveToRelative(7.17f, 2.73f, 9.9f, 0.0f)
        reflectiveCurveToRelative(2.73f, -7.16f, 0.0f, -9.9f)
        close()
        moveTo(16.06f, 16.01f)
        curveToRelative(-2.13f, 2.13f, -5.57f, 2.13f, -7.7f, 0.0f)
        reflectiveCurveToRelative(-2.13f, -5.57f, 0.0f, -7.7f)
        reflectiveCurveToRelative(5.57f, -2.13f, 7.7f, 0.0f)
        reflectiveCurveToRelative(2.13f, 5.57f, 0.0f, 7.7f)
        close()
    }
}
