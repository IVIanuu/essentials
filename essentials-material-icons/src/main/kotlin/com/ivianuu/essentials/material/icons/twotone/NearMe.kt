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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.NearMe: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(11.39f, 12.61f)
        lineToRelative(0.32f, 0.83f)
        lineToRelative(1.32f, 3.42f)
        lineToRelative(4.24f, -10.13f)
        lineToRelative(-10.13f, 4.24f)
        lineToRelative(3.42f, 1.33f)
        close()
    }
    path {
        moveTo(3.0f, 11.51f)
        lineToRelative(6.84f, 2.65f)
        lineTo(12.48f, 21.0f)
        horizontalLineToRelative(0.98f)
        lineTo(21.0f, 3.0f)
        lineTo(3.0f, 10.53f)
        verticalLineToRelative(0.98f)
        close()
        moveTo(17.27f, 6.73f)
        lineToRelative(-4.24f, 10.13f)
        lineToRelative(-1.32f, -3.42f)
        lineToRelative(-0.32f, -0.83f)
        lineToRelative(-0.82f, -0.32f)
        lineToRelative(-3.43f, -1.33f)
        lineToRelative(10.13f, -4.23f)
        close()
    }
}
