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

val Icons.Rounded.ThumbsUpDown: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(10.06f, 5.0f)
        horizontalLineTo(5.82f)
        lineToRelative(0.66f, -3.18f)
        curveToRelative(0.08f, -0.37f, -0.04f, -0.75f, -0.3f, -1.02f)
        curveTo(5.74f, 0.36f, 5.03f, 0.36f, 4.6f, 0.8f)
        lineToRelative(-4.0f, 4.0f)
        curveToRelative(-0.39f, 0.37f, -0.6f, 0.88f, -0.6f, 1.41f)
        verticalLineTo(12.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(5.92f)
        curveToRelative(0.8f, 0.0f, 1.52f, -0.48f, 1.84f, -1.21f)
        lineToRelative(2.14f, -5.0f)
        curveTo(12.46f, 6.47f, 11.49f, 5.0f, 10.06f, 5.0f)
        close()
        moveTo(22.0f, 10.0f)
        horizontalLineToRelative(-5.92f)
        curveToRelative(-0.8f, 0.0f, -1.52f, 0.48f, -1.84f, 1.21f)
        lineToRelative(-2.14f, 5.0f)
        curveToRelative(-0.56f, 1.32f, 0.4f, 2.79f, 1.84f, 2.79f)
        horizontalLineToRelative(4.24f)
        lineToRelative(-0.66f, 3.18f)
        curveToRelative(-0.08f, 0.37f, 0.04f, 0.75f, 0.3f, 1.02f)
        curveToRelative(0.44f, 0.44f, 1.15f, 0.44f, 1.58f, 0.0f)
        lineToRelative(4.0f, -4.0f)
        curveToRelative(0.38f, -0.38f, 0.59f, -0.88f, 0.59f, -1.41f)
        verticalLineTo(12.0f)
        curveToRelative(0.01f, -1.1f, -0.89f, -2.0f, -1.99f, -2.0f)
        close()
    }
}
