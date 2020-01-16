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

val Icons.Outlined.CloudCircle: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.0f)
        curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-4.41f, 0.0f, -8.0f, -3.59f, -8.0f, -8.0f)
        reflectiveCurveToRelative(3.59f, -8.0f, 8.0f, -8.0f)
        reflectiveCurveToRelative(8.0f, 3.59f, 8.0f, 8.0f)
        reflectiveCurveToRelative(-3.59f, 8.0f, -8.0f, 8.0f)
        close()
        moveTo(16.29f, 10.19f)
        curveToRelative(-0.4f, -2.01f, -2.16f, -3.52f, -4.29f, -3.52f)
        curveToRelative(-1.69f, 0.0f, -3.15f, 0.96f, -3.88f, 2.36f)
        curveTo(6.36f, 9.21f, 5.0f, 10.7f, 5.0f, 12.5f)
        curveTo(5.0f, 14.43f, 6.57f, 16.0f, 8.5f, 16.0f)
        horizontalLineToRelative(7.58f)
        curveToRelative(1.61f, 0.0f, 2.92f, -1.31f, 2.92f, -2.92f)
        curveToRelative(0.0f, -1.54f, -1.2f, -2.79f, -2.71f, -2.89f)
        close()
        moveTo(16.0f, 14.0f)
        lineTo(8.5f, 14.0f)
        curveToRelative(-0.83f, 0.0f, -1.5f, -0.67f, -1.5f, -1.5f)
        reflectiveCurveTo(7.67f, 11.0f, 8.5f, 11.0f)
        horizontalLineToRelative(0.9f)
        lineToRelative(0.49f, -1.05f)
        curveToRelative(0.41f, -0.79f, 1.22f, -1.28f, 2.11f, -1.28f)
        curveToRelative(1.13f, 0.0f, 2.11f, 0.8f, 2.33f, 1.91f)
        lineToRelative(0.28f, 1.42f)
        lineTo(16.0f, 12.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
        reflectiveCurveToRelative(-0.45f, 1.0f, -1.0f, 1.0f)
        close()
    }
}
