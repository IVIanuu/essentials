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

package com.ivianuu.essentials.material.icons.twotone

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.Navigation: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(7.72f, 17.7f)
        lineToRelative(3.47f, -1.53f)
        lineToRelative(0.81f, -0.36f)
        lineToRelative(0.81f, 0.36f)
        lineToRelative(3.47f, 1.53f)
        lineTo(12.0f, 7.27f)
        close()
    }
    path {
        moveTo(4.5f, 20.29f)
        lineToRelative(0.71f, 0.71f)
        lineTo(12.0f, 18.0f)
        lineToRelative(6.79f, 3.0f)
        lineToRelative(0.71f, -0.71f)
        lineTo(12.0f, 2.0f)
        lineTo(4.5f, 20.29f)
        close()
        moveTo(12.81f, 16.17f)
        lineToRelative(-0.81f, -0.36f)
        lineToRelative(-0.81f, 0.36f)
        lineToRelative(-3.47f, 1.53f)
        lineTo(12.0f, 7.27f)
        lineToRelative(4.28f, 10.43f)
        lineToRelative(-3.47f, -1.53f)
        close()
    }
}
