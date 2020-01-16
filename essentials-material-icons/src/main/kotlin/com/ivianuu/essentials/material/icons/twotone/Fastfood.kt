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

val Icons.TwoTone.Fastfood: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(1.0f, 21.98f)
        curveToRelative(0.0f, 0.56f, 0.45f, 1.01f, 1.01f, 1.01f)
        horizontalLineTo(15.0f)
        curveToRelative(0.56f, 0.0f, 1.01f, -0.45f, 1.01f, -1.01f)
        verticalLineTo(21.0f)
        horizontalLineTo(1.0f)
        verticalLineToRelative(0.98f)
        close()
    }
    path(fillAlpha = 0.3f) {
        moveTo(8.5f, 10.99f)
        curveToRelative(-1.42f, 0.0f, -3.77f, 0.46f, -4.88f, 2.01f)
        horizontalLineToRelative(9.77f)
        curveToRelative(-1.12f, -1.55f, -3.47f, -2.01f, -4.89f, -2.01f)
        close()
    }
    path {
        moveTo(8.5f, 8.99f)
        curveTo(4.75f, 8.99f, 1.0f, 11.0f, 1.0f, 15.0f)
        horizontalLineToRelative(15.0f)
        curveToRelative(0.0f, -4.0f, -3.75f, -6.01f, -7.5f, -6.01f)
        close()
        moveTo(3.62f, 13.0f)
        curveToRelative(1.11f, -1.55f, 3.47f, -2.01f, 4.88f, -2.01f)
        reflectiveCurveToRelative(3.77f, 0.46f, 4.88f, 2.01f)
        horizontalLineTo(3.62f)
        close()
        moveTo(1.0f, 17.0f)
        horizontalLineToRelative(15.0f)
        verticalLineToRelative(2.0f)
        horizontalLineTo(1.0f)
        close()
        moveTo(18.0f, 5.0f)
        verticalLineTo(1.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(-5.0f)
        lineToRelative(0.23f, 2.0f)
        horizontalLineToRelative(9.56f)
        lineToRelative(-1.4f, 14.0f)
        horizontalLineTo(18.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(1.72f)
        curveToRelative(0.84f, 0.0f, 1.53f, -0.65f, 1.63f, -1.47f)
        lineTo(23.0f, 5.0f)
        horizontalLineToRelative(-5.0f)
        close()
    }
}
