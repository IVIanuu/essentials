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

package com.ivianuu.essentials.material.icons.filled

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Filled.NoEncryption: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(21.0f, 21.78f)
        lineTo(4.22f, 5.0f)
        lineTo(3.0f, 6.22f)
        lineToRelative(2.04f, 2.04f)
        curveTo(4.42f, 8.6f, 4.0f, 9.25f, 4.0f, 10.0f)
        verticalLineToRelative(10.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(0.23f, 0.0f, 0.45f, -0.05f, 0.66f, -0.12f)
        lineTo(19.78f, 23.0f)
        lineTo(21.0f, 21.78f)
        close()
        moveTo(8.9f, 6.0f)
        curveToRelative(0.0f, -1.71f, 1.39f, -3.1f, 3.1f, -3.1f)
        reflectiveCurveToRelative(3.1f, 1.39f, 3.1f, 3.1f)
        verticalLineToRelative(2.0f)
        horizontalLineTo(9.66f)
        lineTo(20.0f, 18.34f)
        verticalLineTo(10.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        horizontalLineToRelative(-1.0f)
        verticalLineTo(6.0f)
        curveToRelative(0.0f, -2.76f, -2.24f, -5.0f, -5.0f, -5.0f)
        curveToRelative(-2.56f, 0.0f, -4.64f, 1.93f, -4.94f, 4.4f)
        lineTo(8.9f, 7.24f)
        verticalLineTo(6.0f)
        close()
    }
}
