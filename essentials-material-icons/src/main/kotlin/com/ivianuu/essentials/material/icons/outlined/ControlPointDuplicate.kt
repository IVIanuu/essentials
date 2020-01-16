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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Outlined.ControlPointDuplicate: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(16.0f, 8.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(-3.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(3.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-3.0f)
        horizontalLineToRelative(3.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(-3.0f)
        lineTo(16.0f, 8.0f)
        close()
        moveTo(2.0f, 12.0f)
        curveToRelative(0.0f, -2.79f, 1.64f, -5.2f, 4.01f, -6.32f)
        lineTo(6.01f, 3.52f)
        curveTo(2.52f, 4.76f, 0.0f, 8.09f, 0.0f, 12.0f)
        reflectiveCurveToRelative(2.52f, 7.24f, 6.01f, 8.48f)
        verticalLineToRelative(-2.16f)
        curveTo(3.64f, 17.2f, 2.0f, 14.79f, 2.0f, 12.0f)
        close()
        moveTo(15.0f, 3.0f)
        curveToRelative(-4.96f, 0.0f, -9.0f, 4.04f, -9.0f, 9.0f)
        reflectiveCurveToRelative(4.04f, 9.0f, 9.0f, 9.0f)
        reflectiveCurveToRelative(9.0f, -4.04f, 9.0f, -9.0f)
        reflectiveCurveToRelative(-4.04f, -9.0f, -9.0f, -9.0f)
        close()
        moveTo(15.0f, 19.0f)
        curveToRelative(-3.86f, 0.0f, -7.0f, -3.14f, -7.0f, -7.0f)
        reflectiveCurveToRelative(3.14f, -7.0f, 7.0f, -7.0f)
        reflectiveCurveToRelative(7.0f, 3.14f, 7.0f, 7.0f)
        reflectiveCurveToRelative(-3.14f, 7.0f, -7.0f, 7.0f)
        close()
    }
}
