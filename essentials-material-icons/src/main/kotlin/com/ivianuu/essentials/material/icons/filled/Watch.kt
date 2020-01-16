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

val Icons.Filled.Watch: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.0f, 12.0f)
        curveToRelative(0.0f, -2.54f, -1.19f, -4.81f, -3.04f, -6.27f)
        lineTo(16.0f, 0.0f)
        horizontalLineTo(8.0f)
        lineToRelative(-0.95f, 5.73f)
        curveTo(5.19f, 7.19f, 4.0f, 9.45f, 4.0f, 12.0f)
        reflectiveCurveToRelative(1.19f, 4.81f, 3.05f, 6.27f)
        lineTo(8.0f, 24.0f)
        horizontalLineToRelative(8.0f)
        lineToRelative(0.96f, -5.73f)
        curveTo(18.81f, 16.81f, 20.0f, 14.54f, 20.0f, 12.0f)
        close()
        moveTo(6.0f, 12.0f)
        curveToRelative(0.0f, -3.31f, 2.69f, -6.0f, 6.0f, -6.0f)
        reflectiveCurveToRelative(6.0f, 2.69f, 6.0f, 6.0f)
        reflectiveCurveToRelative(-2.69f, 6.0f, -6.0f, 6.0f)
        reflectiveCurveToRelative(-6.0f, -2.69f, -6.0f, -6.0f)
        close()
    }
}
