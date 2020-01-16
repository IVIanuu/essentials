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

val Icons.Rounded.TextRotateVertical: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(15.0f, 5.0f)
        curveToRelative(-0.46f, 0.0f, -0.87f, 0.27f, -1.05f, 0.69f)
        lineToRelative(-3.88f, 8.97f)
        curveToRelative(-0.27f, 0.63f, 0.2f, 1.34f, 0.89f, 1.34f)
        curveToRelative(0.39f, 0.0f, 0.74f, -0.24f, 0.89f, -0.6f)
        lineToRelative(0.66f, -1.6f)
        horizontalLineToRelative(5.0f)
        lineToRelative(0.66f, 1.6f)
        curveToRelative(0.15f, 0.36f, 0.5f, 0.6f, 0.89f, 0.6f)
        curveToRelative(0.69f, 0.0f, 1.15f, -0.71f, 0.88f, -1.34f)
        lineToRelative(-3.88f, -8.97f)
        curveTo(15.87f, 5.27f, 15.46f, 5.0f, 15.0f, 5.0f)
        close()
        moveTo(13.13f, 12.0f)
        lineTo(15.0f, 6.98f)
        lineTo(16.87f, 12.0f)
        horizontalLineToRelative(-3.74f)
        close()
        moveTo(6.35f, 19.64f)
        lineToRelative(1.79f, -1.79f)
        curveToRelative(0.32f, -0.31f, 0.1f, -0.85f, -0.35f, -0.85f)
        lineTo(7.0f, 17.0f)
        lineTo(7.0f, 5.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        reflectiveCurveToRelative(-1.0f, 0.44f, -1.0f, 1.0f)
        verticalLineToRelative(12.0f)
        horizontalLineToRelative(-0.79f)
        curveToRelative(-0.45f, 0.0f, -0.67f, 0.54f, -0.35f, 0.85f)
        lineToRelative(1.79f, 1.79f)
        curveToRelative(0.19f, 0.2f, 0.51f, 0.2f, 0.7f, 0.0f)
        close()
    }
}
