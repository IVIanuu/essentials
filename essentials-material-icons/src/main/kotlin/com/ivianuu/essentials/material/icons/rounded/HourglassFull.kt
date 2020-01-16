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

val Icons.Rounded.HourglassFull: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(6.0f, 4.0f)
        verticalLineToRelative(3.17f)
        curveToRelative(0.0f, 0.53f, 0.21f, 1.04f, 0.59f, 1.42f)
        lineTo(10.0f, 12.0f)
        lineToRelative(-3.42f, 3.42f)
        curveToRelative(-0.37f, 0.38f, -0.58f, 0.89f, -0.58f, 1.42f)
        verticalLineTo(20.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(8.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineToRelative(-3.16f)
        curveToRelative(0.0f, -0.53f, -0.21f, -1.04f, -0.58f, -1.41f)
        lineTo(14.0f, 12.0f)
        lineToRelative(3.41f, -3.4f)
        curveToRelative(0.38f, -0.38f, 0.59f, -0.89f, 0.59f, -1.42f)
        verticalLineTo(4.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        horizontalLineTo(8.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        close()
    }
}
