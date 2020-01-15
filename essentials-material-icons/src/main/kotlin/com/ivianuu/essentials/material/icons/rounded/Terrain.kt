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

val Icons.Rounded.Terrain: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(13.2f, 7.07f)
        lineTo(10.25f, 11.0f)
        lineToRelative(2.25f, 3.0f)
        curveToRelative(0.33f, 0.44f, 0.24f, 1.07f, -0.2f, 1.4f)
        curveToRelative(-0.44f, 0.33f, -1.07f, 0.25f, -1.4f, -0.2f)
        curveToRelative(-1.05f, -1.4f, -2.31f, -3.07f, -3.1f, -4.14f)
        curveToRelative(-0.4f, -0.53f, -1.2f, -0.53f, -1.6f, 0.0f)
        lineToRelative(-4.0f, 5.33f)
        curveToRelative(-0.49f, 0.67f, -0.02f, 1.61f, 0.8f, 1.61f)
        horizontalLineToRelative(18.0f)
        curveToRelative(0.82f, 0.0f, 1.29f, -0.94f, 0.8f, -1.6f)
        lineToRelative(-7.0f, -9.33f)
        curveToRelative(-0.4f, -0.54f, -1.2f, -0.54f, -1.6f, 0.0f)
        close()
    }
}
