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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.Bathtub: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(7.0f, 7.0f)
        moveToRelative(-2.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
    }
    path {
        moveTo(20.0f, 13.0f)
        verticalLineTo(4.83f)
        curveTo(20.0f, 3.27f, 18.73f, 2.0f, 17.17f, 2.0f)
        curveToRelative(-0.75f, 0.0f, -1.47f, 0.3f, -2.0f, 0.83f)
        lineToRelative(-1.25f, 1.25f)
        curveTo(13.76f, 4.03f, 13.59f, 4.0f, 13.41f, 4.0f)
        curveToRelative(-0.4f, 0.0f, -0.77f, 0.12f, -1.08f, 0.32f)
        lineToRelative(2.76f, 2.76f)
        curveToRelative(0.2f, -0.31f, 0.32f, -0.68f, 0.32f, -1.08f)
        curveToRelative(0.0f, -0.18f, -0.03f, -0.34f, -0.07f, -0.51f)
        lineToRelative(1.25f, -1.25f)
        curveTo(16.74f, 4.09f, 16.95f, 4.0f, 17.17f, 4.0f)
        curveTo(17.63f, 4.0f, 18.0f, 4.37f, 18.0f, 4.83f)
        verticalLineTo(13.0f)
        horizontalLineToRelative(-6.85f)
        curveToRelative(-0.3f, -0.21f, -0.57f, -0.45f, -0.82f, -0.72f)
        lineToRelative(-1.4f, -1.55f)
        curveToRelative(-0.19f, -0.21f, -0.43f, -0.38f, -0.69f, -0.5f)
        curveTo(7.93f, 10.08f, 7.59f, 10.0f, 7.24f, 10.0f)
        curveTo(6.0f, 10.01f, 5.0f, 11.01f, 5.0f, 12.25f)
        verticalLineTo(13.0f)
        horizontalLineTo(2.0f)
        verticalLineToRelative(6.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineToRelative(-6.0f)
        horizontalLineTo(20.0f)
        close()
    }
}
