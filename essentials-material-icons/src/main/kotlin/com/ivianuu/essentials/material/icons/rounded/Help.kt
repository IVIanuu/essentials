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

val Icons.Rounded.Help: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.0f)
        curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(13.0f, 19.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(15.07f, 11.25f)
        lineToRelative(-0.9f, 0.92f)
        curveToRelative(-0.5f, 0.51f, -0.86f, 0.97f, -1.04f, 1.69f)
        curveToRelative(-0.08f, 0.32f, -0.13f, 0.68f, -0.13f, 1.14f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-0.5f)
        curveToRelative(0.0f, -0.46f, 0.08f, -0.9f, 0.22f, -1.31f)
        curveToRelative(0.2f, -0.58f, 0.53f, -1.1f, 0.95f, -1.52f)
        lineToRelative(1.24f, -1.26f)
        curveToRelative(0.46f, -0.44f, 0.68f, -1.1f, 0.55f, -1.8f)
        curveToRelative(-0.13f, -0.72f, -0.69f, -1.33f, -1.39f, -1.53f)
        curveToRelative(-1.11f, -0.31f, -2.14f, 0.32f, -2.47f, 1.27f)
        curveToRelative(-0.12f, 0.37f, -0.43f, 0.65f, -0.82f, 0.65f)
        horizontalLineToRelative(-0.3f)
        curveTo(8.4f, 9.0f, 8.0f, 8.44f, 8.16f, 7.88f)
        curveToRelative(0.43f, -1.47f, 1.68f, -2.59f, 3.23f, -2.83f)
        curveToRelative(1.52f, -0.24f, 2.97f, 0.55f, 3.87f, 1.8f)
        curveToRelative(1.18f, 1.63f, 0.83f, 3.38f, -0.19f, 4.4f)
        close()
    }
}
