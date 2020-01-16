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

package com.ivianuu.essentials.material.icons.twotone

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.TwoTone.Rowing: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(8.5f, 14.5f)
        lineTo(4.0f, 19.0f)
        lineToRelative(1.5f, 1.5f)
        lineTo(9.0f, 17.0f)
        horizontalLineToRelative(2.0f)
        lineToRelative(-2.5f, -2.5f)
        close()
        moveTo(15.0f, 1.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        reflectiveCurveToRelative(0.9f, 2.0f, 2.0f, 2.0f)
        reflectiveCurveToRelative(2.0f, -0.9f, 2.0f, -2.0f)
        reflectiveCurveToRelative(-0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(21.0f, 21.01f)
        lineTo(18.0f, 24.0f)
        lineToRelative(-2.99f, -3.01f)
        lineTo(15.01f, 19.5f)
        lineToRelative(-7.1f, -7.09f)
        curveToRelative(-0.31f, 0.05f, -0.61f, 0.07f, -0.91f, 0.07f)
        verticalLineToRelative(-2.16f)
        curveToRelative(1.66f, 0.03f, 3.61f, -0.87f, 4.67f, -2.04f)
        lineToRelative(1.4f, -1.55f)
        curveToRelative(0.19f, -0.21f, 0.43f, -0.38f, 0.69f, -0.5f)
        curveToRelative(0.29f, -0.14f, 0.62f, -0.23f, 0.96f, -0.23f)
        horizontalLineToRelative(0.03f)
        curveTo(15.99f, 6.01f, 17.0f, 7.02f, 17.0f, 8.26f)
        verticalLineToRelative(5.75f)
        curveToRelative(0.0f, 0.84f, -0.35f, 1.61f, -0.92f, 2.16f)
        lineToRelative(-3.58f, -3.58f)
        verticalLineToRelative(-2.27f)
        curveToRelative(-0.63f, 0.52f, -1.43f, 1.02f, -2.29f, 1.39f)
        lineTo(16.5f, 18.0f)
        lineTo(18.0f, 18.0f)
        lineToRelative(3.0f, 3.01f)
        close()
    }
}
