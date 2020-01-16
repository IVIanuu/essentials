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

val Icons.Rounded.Redo: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.4f, 10.6f)
        curveTo(16.55f, 8.99f, 14.15f, 8.0f, 11.5f, 8.0f)
        curveToRelative(-4.16f, 0.0f, -7.74f, 2.42f, -9.44f, 5.93f)
        curveToRelative(-0.32f, 0.67f, 0.04f, 1.47f, 0.75f, 1.71f)
        curveToRelative(0.59f, 0.2f, 1.23f, -0.08f, 1.5f, -0.64f)
        curveToRelative(1.3f, -2.66f, 4.03f, -4.5f, 7.19f, -4.5f)
        curveToRelative(1.95f, 0.0f, 3.73f, 0.72f, 5.12f, 1.88f)
        lineToRelative(-1.91f, 1.91f)
        curveToRelative(-0.63f, 0.63f, -0.19f, 1.71f, 0.7f, 1.71f)
        horizontalLineTo(21.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineTo(9.41f)
        curveToRelative(0.0f, -0.89f, -1.08f, -1.34f, -1.71f, -0.71f)
        lineToRelative(-1.89f, 1.9f)
        close()
    }
}
