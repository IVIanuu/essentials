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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.Radio: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(20.0f, 13.0f)
        horizontalLineTo(4.0f)
        verticalLineToRelative(7.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(-7.0f)
        close()
        moveTo(8.0f, 18.98f)
        curveToRelative(-1.38f, 0.0f, -2.5f, -1.12f, -2.5f, -2.5f)
        reflectiveCurveToRelative(1.12f, -2.5f, 2.5f, -2.5f)
        reflectiveCurveToRelative(2.5f, 1.12f, 2.5f, 2.5f)
        reflectiveCurveToRelative(-1.12f, 2.5f, -2.5f, 2.5f)
        close()
    }
    path {
        moveTo(2.0f, 20.0f)
        curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(1.11f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(22.0f, 8.0f)
        curveToRelative(0.0f, -1.11f, -0.89f, -2.0f, -2.0f, -2.0f)
        lineTo(8.3f, 6.0f)
        lineToRelative(8.26f, -3.34f)
        lineTo(15.88f, 1.0f)
        lineTo(3.24f, 6.15f)
        curveTo(2.51f, 6.43f, 2.0f, 7.17f, 2.0f, 8.0f)
        verticalLineToRelative(12.0f)
        close()
        moveTo(4.0f, 8.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(-2.0f)
        lineTo(18.0f, 9.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(2.0f)
        lineTo(4.0f, 11.0f)
        lineTo(4.0f, 8.0f)
        close()
        moveTo(4.0f, 13.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(7.0f)
        lineTo(4.0f, 20.0f)
        verticalLineToRelative(-7.0f)
        close()
    }
    path {
        moveTo(8.0f, 16.48f)
        moveToRelative(-2.5f, 0.0f)
        arcToRelative(2.5f, 2.5f, 0.0f, true, true, 5.0f, 0.0f)
        arcToRelative(2.5f, 2.5f, 0.0f, true, true, -5.0f, 0.0f)
    }
}
