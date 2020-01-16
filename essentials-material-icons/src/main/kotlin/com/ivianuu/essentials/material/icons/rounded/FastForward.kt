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

val Icons.Rounded.FastForward: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(5.58f, 16.89f)
        lineToRelative(5.77f, -4.07f)
        curveToRelative(0.56f, -0.4f, 0.56f, -1.24f, 0.0f, -1.63f)
        lineTo(5.58f, 7.11f)
        curveTo(4.91f, 6.65f, 4.0f, 7.12f, 4.0f, 7.93f)
        verticalLineToRelative(8.14f)
        curveToRelative(0.0f, 0.81f, 0.91f, 1.28f, 1.58f, 0.82f)
        close()
        moveTo(13.0f, 7.93f)
        verticalLineToRelative(8.14f)
        curveToRelative(0.0f, 0.81f, 0.91f, 1.28f, 1.58f, 0.82f)
        lineToRelative(5.77f, -4.07f)
        curveToRelative(0.56f, -0.4f, 0.56f, -1.24f, 0.0f, -1.63f)
        lineToRelative(-5.77f, -4.07f)
        curveToRelative(-0.67f, -0.47f, -1.58f, 0.0f, -1.58f, 0.81f)
        close()
    }
}
