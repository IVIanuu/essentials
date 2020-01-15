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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.Favorite: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 21.35f)
        lineToRelative(-1.45f, -1.32f)
        curveTo(5.4f, 15.36f, 2.0f, 12.28f, 2.0f, 8.5f)
        curveTo(2.0f, 5.42f, 4.42f, 3.0f, 7.5f, 3.0f)
        curveToRelative(1.74f, 0.0f, 3.41f, 0.81f, 4.5f, 2.09f)
        curveTo(13.09f, 3.81f, 14.76f, 3.0f, 16.5f, 3.0f)
        curveTo(19.58f, 3.0f, 22.0f, 5.42f, 22.0f, 8.5f)
        curveToRelative(0.0f, 3.78f, -3.4f, 6.86f, -8.55f, 11.54f)
        lineTo(12.0f, 21.35f)
        close()
    }
}
