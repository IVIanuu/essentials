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

val Icons.Rounded.Panorama: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(23.0f, 18.0f)
        verticalLineTo(6.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        horizontalLineTo(3.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(18.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        close()
        moveTo(8.9f, 12.98f)
        lineToRelative(2.1f, 2.53f)
        lineToRelative(3.1f, -3.99f)
        curveToRelative(0.2f, -0.26f, 0.6f, -0.26f, 0.8f, 0.01f)
        lineToRelative(3.51f, 4.68f)
        curveToRelative(0.25f, 0.33f, 0.01f, 0.8f, -0.4f, 0.8f)
        horizontalLineTo(6.02f)
        curveToRelative(-0.42f, 0.0f, -0.65f, -0.48f, -0.39f, -0.81f)
        lineTo(8.12f, 13.0f)
        curveToRelative(0.19f, -0.26f, 0.57f, -0.27f, 0.78f, -0.02f)
        close()
    }
}
