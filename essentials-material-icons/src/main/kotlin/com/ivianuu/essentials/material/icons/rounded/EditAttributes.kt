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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Rounded.EditAttributes: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(17.63f, 7.0f)
        lineTo(6.37f, 7.0f)
        curveTo(3.96f, 7.0f, 2.0f, 9.24f, 2.0f, 12.0f)
        reflectiveCurveToRelative(1.96f, 5.0f, 4.37f, 5.0f)
        horizontalLineToRelative(11.26f)
        curveToRelative(2.41f, 0.0f, 4.37f, -2.24f, 4.37f, -5.0f)
        reflectiveCurveToRelative(-1.96f, -5.0f, -4.37f, -5.0f)
        close()
        moveTo(11.11f, 10.6f)
        lineTo(7.6f, 14.11f)
        curveToRelative(-0.1f, 0.1f, -0.23f, 0.15f, -0.35f, 0.15f)
        reflectiveCurveToRelative(-0.26f, -0.05f, -0.35f, -0.15f)
        lineToRelative(-1.86f, -1.86f)
        curveToRelative(-0.2f, -0.2f, -0.2f, -0.51f, 0.0f, -0.71f)
        reflectiveCurveToRelative(0.51f, -0.2f, 0.71f, 0.0f)
        lineToRelative(1.51f, 1.51f)
        lineToRelative(3.16f, -3.16f)
        curveToRelative(0.2f, -0.2f, 0.51f, -0.2f, 0.71f, 0.0f)
        reflectiveCurveToRelative(0.17f, 0.51f, -0.02f, 0.71f)
        close()
    }
}
