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

val Icons.TwoTone.LocalOffer: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(11.0f, 4.0f)
        horizontalLineTo(4.0f)
        verticalLineToRelative(7.0f)
        lineToRelative(9.0f, 9.01f)
        lineTo(20.0f, 13.0f)
        lineToRelative(-9.0f, -9.0f)
        close()
        moveTo(6.5f, 8.0f)
        curveTo(5.67f, 8.0f, 5.0f, 7.33f, 5.0f, 6.5f)
        reflectiveCurveTo(5.67f, 5.0f, 6.5f, 5.0f)
        reflectiveCurveTo(8.0f, 5.67f, 8.0f, 6.5f)
        reflectiveCurveTo(7.33f, 8.0f, 6.5f, 8.0f)
        close()
    }
    path {
        moveTo(12.41f, 2.58f)
        curveTo(12.05f, 2.22f, 11.55f, 2.0f, 11.0f, 2.0f)
        horizontalLineTo(4.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(7.0f)
        curveToRelative(0.0f, 0.55f, 0.22f, 1.05f, 0.59f, 1.42f)
        lineToRelative(9.0f, 9.0f)
        curveToRelative(0.36f, 0.36f, 0.86f, 0.58f, 1.41f, 0.58f)
        reflectiveCurveToRelative(1.05f, -0.22f, 1.41f, -0.59f)
        lineToRelative(7.0f, -7.0f)
        curveToRelative(0.37f, -0.36f, 0.59f, -0.86f, 0.59f, -1.41f)
        reflectiveCurveToRelative(-0.23f, -1.06f, -0.59f, -1.42f)
        lineToRelative(-9.0f, -9.0f)
        close()
        moveTo(13.0f, 20.01f)
        lineTo(4.0f, 11.0f)
        verticalLineTo(4.0f)
        horizontalLineToRelative(7.0f)
        verticalLineToRelative(-0.01f)
        lineToRelative(9.0f, 9.0f)
        lineToRelative(-7.0f, 7.02f)
        close()
    }
    path {
        moveTo(6.5f, 6.5f)
        moveToRelative(-1.5f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, 3.0f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, -3.0f, 0.0f)
    }
}
