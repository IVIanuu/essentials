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

val Icons.Rounded.Timer: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(14.0f, 1.0f)
        horizontalLineToRelative(-4.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        reflectiveCurveToRelative(0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(4.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
        close()
        moveTo(12.0f, 14.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        lineTo(13.0f, 9.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        reflectiveCurveToRelative(-1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(4.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        close()
        moveTo(19.03f, 7.39f)
        lineToRelative(0.75f, -0.75f)
        curveToRelative(0.38f, -0.38f, 0.39f, -1.01f, 0.0f, -1.4f)
        lineToRelative(-0.01f, -0.01f)
        curveToRelative(-0.39f, -0.39f, -1.01f, -0.38f, -1.4f, 0.0f)
        lineToRelative(-0.75f, 0.75f)
        curveTo(16.07f, 4.74f, 14.12f, 4.0f, 12.0f, 4.0f)
        curveToRelative(-4.8f, 0.0f, -8.88f, 3.96f, -9.0f, 8.76f)
        curveTo(2.87f, 17.84f, 6.94f, 22.0f, 12.0f, 22.0f)
        curveToRelative(4.98f, 0.0f, 9.0f, -4.03f, 9.0f, -9.0f)
        curveToRelative(0.0f, -2.12f, -0.74f, -4.07f, -1.97f, -5.61f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-3.87f, 0.0f, -7.0f, -3.13f, -7.0f, -7.0f)
        reflectiveCurveToRelative(3.13f, -7.0f, 7.0f, -7.0f)
        reflectiveCurveToRelative(7.0f, 3.13f, 7.0f, 7.0f)
        reflectiveCurveToRelative(-3.13f, 7.0f, -7.0f, 7.0f)
        close()
    }
}
