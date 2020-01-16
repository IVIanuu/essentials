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

val Icons.TwoTone.Layers: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(6.26f, 9.0f)
        lineTo(12.0f, 13.47f)
        lineTo(17.74f, 9.0f)
        lineTo(12.0f, 4.53f)
        close()
    }
    path {
        moveTo(19.37f, 12.8f)
        lineToRelative(-7.38f, 5.74f)
        lineToRelative(-7.37f, -5.73f)
        lineTo(3.0f, 14.07f)
        lineToRelative(9.0f, 7.0f)
        lineToRelative(9.0f, -7.0f)
        close()
        moveTo(12.0f, 2.0f)
        lineTo(3.0f, 9.0f)
        lineToRelative(1.63f, 1.27f)
        lineTo(12.0f, 16.0f)
        lineToRelative(7.36f, -5.73f)
        lineTo(21.0f, 9.0f)
        lineToRelative(-9.0f, -7.0f)
        close()
        moveTo(12.0f, 13.47f)
        lineTo(6.26f, 9.0f)
        lineTo(12.0f, 4.53f)
        lineTo(17.74f, 9.0f)
        lineTo(12.0f, 13.47f)
        close()
    }
}
