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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.Undo: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.5f, 8.0f)
        curveToRelative(-2.65f, 0.0f, -5.05f, 0.99f, -6.9f, 2.6f)
        lineTo(3.71f, 8.71f)
        curveTo(3.08f, 8.08f, 2.0f, 8.52f, 2.0f, 9.41f)
        verticalLineTo(15.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(5.59f)
        curveToRelative(0.89f, 0.0f, 1.34f, -1.08f, 0.71f, -1.71f)
        lineToRelative(-1.91f, -1.91f)
        curveToRelative(1.39f, -1.16f, 3.16f, -1.88f, 5.12f, -1.88f)
        curveToRelative(3.16f, 0.0f, 5.89f, 1.84f, 7.19f, 4.5f)
        curveToRelative(0.27f, 0.56f, 0.91f, 0.84f, 1.5f, 0.64f)
        curveToRelative(0.71f, -0.23f, 1.07f, -1.04f, 0.75f, -1.72f)
        curveTo(20.23f, 10.42f, 16.65f, 8.0f, 12.5f, 8.0f)
        close()
    }
}
