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

val Icons.TwoTone.PersonOutline: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 8.0f)
        moveToRelative(-2.1f, 0.0f)
        arcToRelative(2.1f, 2.1f, 0.0f, true, true, 4.2f, 0.0f)
        arcToRelative(2.1f, 2.1f, 0.0f, true, true, -4.2f, 0.0f)
    }
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 14.9f)
        curveToRelative(-2.97f, 0.0f, -6.1f, 1.46f, -6.1f, 2.1f)
        verticalLineToRelative(1.1f)
        horizontalLineToRelative(12.2f)
        verticalLineTo(17.0f)
        curveToRelative(0.0f, -0.64f, -3.13f, -2.1f, -6.1f, -2.1f)
        close()
    }
    path {
        moveTo(12.0f, 13.0f)
        curveToRelative(-2.67f, 0.0f, -8.0f, 1.34f, -8.0f, 4.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(-3.0f)
        curveToRelative(0.0f, -2.66f, -5.33f, -4.0f, -8.0f, -4.0f)
        close()
        moveTo(18.1f, 18.1f)
        lineTo(5.9f, 18.1f)
        lineTo(5.9f, 17.0f)
        curveToRelative(0.0f, -0.64f, 3.13f, -2.1f, 6.1f, -2.1f)
        reflectiveCurveToRelative(6.1f, 1.46f, 6.1f, 2.1f)
        verticalLineToRelative(1.1f)
        close()
        moveTo(12.0f, 12.0f)
        curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
        reflectiveCurveToRelative(-1.79f, -4.0f, -4.0f, -4.0f)
        reflectiveCurveToRelative(-4.0f, 1.79f, -4.0f, 4.0f)
        reflectiveCurveToRelative(1.79f, 4.0f, 4.0f, 4.0f)
        close()
        moveTo(12.0f, 5.9f)
        curveToRelative(1.16f, 0.0f, 2.1f, 0.94f, 2.1f, 2.1f)
        curveToRelative(0.0f, 1.16f, -0.94f, 2.1f, -2.1f, 2.1f)
        reflectiveCurveTo(9.9f, 9.16f, 9.9f, 8.0f)
        curveToRelative(0.0f, -1.16f, 0.94f, -2.1f, 2.1f, -2.1f)
        close()
    }
}
