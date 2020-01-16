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

val Icons.Sharp.PhotoAlbum: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.0f, 2.0f)
        lineTo(4.0f, 2.0f)
        verticalLineToRelative(20.0f)
        horizontalLineToRelative(16.0f)
        lineTo(20.0f, 2.0f)
        close()
        moveTo(6.0f, 4.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(8.0f)
        lineToRelative(-2.5f, -1.5f)
        lineTo(6.0f, 12.0f)
        lineTo(6.0f, 4.0f)
        close()
        moveTo(6.0f, 19.0f)
        lineToRelative(3.0f, -3.86f)
        lineToRelative(2.14f, 2.58f)
        lineToRelative(3.0f, -3.86f)
        lineTo(18.0f, 19.0f)
        lineTo(6.0f, 19.0f)
        close()
    }
}
