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

val Icons.Sharp.FirstPage: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.41f, 16.59f)
        lineTo(13.82f, 12.0f)
        lineToRelative(4.59f, -4.59f)
        lineTo(17.0f, 6.0f)
        lineToRelative(-6.0f, 6.0f)
        lineToRelative(6.0f, 6.0f)
        lineToRelative(1.41f, -1.41f)
        close()
        moveTo(6.0f, 6.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(12.0f)
        horizontalLineTo(6.0f)
        verticalLineTo(6.0f)
        close()
    }
}
