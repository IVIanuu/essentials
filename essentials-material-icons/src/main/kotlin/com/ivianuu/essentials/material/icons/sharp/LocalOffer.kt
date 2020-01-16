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

package com.ivianuu.essentials.material.icons.sharp

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Sharp.LocalOffer: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(22.83f, 12.99f)
        lineTo(11.83f, 2.0f)
        horizontalLineTo(2.0f)
        verticalLineToRelative(9.83f)
        lineToRelative(10.99f, 10.99f)
        lineToRelative(9.84f, -9.83f)
        close()
        moveTo(5.5f, 7.0f)
        curveTo(4.67f, 7.0f, 4.0f, 6.33f, 4.0f, 5.5f)
        reflectiveCurveTo(4.67f, 4.0f, 5.5f, 4.0f)
        reflectiveCurveTo(7.0f, 4.67f, 7.0f, 5.5f)
        reflectiveCurveTo(6.33f, 7.0f, 5.5f, 7.0f)
        close()
    }
}
