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

val Icons.Rounded.Watch: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.0f, 12.0f)
        curveToRelative(0.0f, -2.54f, -1.19f, -4.81f, -3.04f, -6.27f)
        lineToRelative(-0.68f, -4.06f)
        curveTo(16.12f, 0.71f, 15.28f, 0.0f, 14.31f, 0.0f)
        horizontalLineTo(9.7f)
        curveToRelative(-0.98f, 0.0f, -1.82f, 0.71f, -1.98f, 1.67f)
        lineToRelative(-0.67f, 4.06f)
        curveTo(5.19f, 7.19f, 4.0f, 9.45f, 4.0f, 12.0f)
        reflectiveCurveToRelative(1.19f, 4.81f, 3.05f, 6.27f)
        lineToRelative(0.67f, 4.06f)
        curveToRelative(0.16f, 0.96f, 1.0f, 1.67f, 1.98f, 1.67f)
        horizontalLineToRelative(4.61f)
        curveToRelative(0.98f, 0.0f, 1.81f, -0.71f, 1.97f, -1.67f)
        lineToRelative(0.68f, -4.06f)
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
