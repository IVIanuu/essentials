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

val Icons.Rounded.CompareArrows: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(9.01f, 14.0f)
        lineTo(3.0f, 14.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        reflectiveCurveToRelative(0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(6.01f)
        verticalLineToRelative(1.79f)
        curveToRelative(0.0f, 0.45f, 0.54f, 0.67f, 0.85f, 0.35f)
        lineToRelative(2.78f, -2.79f)
        curveToRelative(0.19f, -0.2f, 0.19f, -0.51f, 0.0f, -0.71f)
        lineToRelative(-2.78f, -2.79f)
        curveToRelative(-0.31f, -0.32f, -0.85f, -0.09f, -0.85f, 0.35f)
        lineTo(9.01f, 14.0f)
        close()
        moveTo(14.99f, 11.79f)
        lineTo(14.99f, 10.0f)
        lineTo(21.0f, 10.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
        horizontalLineToRelative(-6.01f)
        lineTo(14.99f, 6.21f)
        curveToRelative(0.0f, -0.45f, -0.54f, -0.67f, -0.85f, -0.35f)
        lineToRelative(-2.78f, 2.79f)
        curveToRelative(-0.19f, 0.2f, -0.19f, 0.51f, 0.0f, 0.71f)
        lineToRelative(2.78f, 2.79f)
        curveToRelative(0.31f, 0.31f, 0.85f, 0.09f, 0.85f, -0.36f)
        close()
    }
}
