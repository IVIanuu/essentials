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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.Height: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(13.0f, 6.99f)
        horizontalLineToRelative(1.79f)
        curveToRelative(0.45f, 0.0f, 0.67f, -0.54f, 0.35f, -0.85f)
        lineToRelative(-2.79f, -2.78f)
        curveToRelative(-0.2f, -0.19f, -0.51f, -0.19f, -0.71f, 0.0f)
        lineTo(8.86f, 6.14f)
        curveTo(8.54f, 6.45f, 8.76f, 6.99f, 9.21f, 6.99f)
        horizontalLineTo(11.0f)
        verticalLineToRelative(10.02f)
        horizontalLineTo(9.21f)
        curveToRelative(-0.45f, 0.0f, -0.67f, 0.54f, -0.35f, 0.85f)
        lineToRelative(2.79f, 2.78f)
        curveToRelative(0.2f, 0.19f, 0.51f, 0.19f, 0.71f, 0.0f)
        lineToRelative(2.79f, -2.78f)
        curveToRelative(0.32f, -0.31f, 0.09f, -0.85f, -0.35f, -0.85f)
        horizontalLineTo(13.0f)
        verticalLineTo(6.99f)
        close()
    }
}
