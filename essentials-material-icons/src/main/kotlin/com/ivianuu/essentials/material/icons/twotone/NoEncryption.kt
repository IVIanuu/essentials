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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.NoEncryption: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(6.0f, 20.0f)
        horizontalLineToRelative(10.78f)
        lineToRelative(-10.0f, -10.0f)
        lineTo(6.0f, 10.0f)
        close()
        moveTo(12.44f, 10.0f)
        lineTo(18.0f, 15.56f)
        lineTo(18.0f, 10.0f)
        close()
    }
    path {
        moveTo(8.9f, 6.0f)
        curveToRelative(0.0f, -1.71f, 1.39f, -3.1f, 3.1f, -3.1f)
        reflectiveCurveToRelative(3.1f, 1.39f, 3.1f, 3.1f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-4.66f)
        lineToRelative(2.0f, 2.0f)
        horizontalLineTo(18.0f)
        verticalLineToRelative(5.56f)
        lineToRelative(2.0f, 2.0f)
        verticalLineTo(10.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        horizontalLineToRelative(-1.0f)
        verticalLineTo(6.0f)
        curveToRelative(0.0f, -2.76f, -2.24f, -5.0f, -5.0f, -5.0f)
        curveToRelative(-2.32f, 0.0f, -4.26f, 1.59f, -4.82f, 3.74f)
        lineTo(8.9f, 6.46f)
        verticalLineTo(6.0f)
        close()
        moveTo(4.41f, 4.81f)
        lineTo(3.0f, 6.22f)
        lineToRelative(2.04f, 2.04f)
        curveTo(4.42f, 8.6f, 4.0f, 9.25f, 4.0f, 10.0f)
        verticalLineToRelative(10.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(12.78f)
        lineToRelative(1.0f, 1.0f)
        lineToRelative(1.41f, -1.41f)
        lineTo(4.41f, 4.81f)
        close()
        moveTo(6.0f, 20.0f)
        verticalLineTo(10.0f)
        horizontalLineToRelative(0.78f)
        lineToRelative(10.0f, 10.0f)
        horizontalLineTo(6.0f)
        close()
    }
}
