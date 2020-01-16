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
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.PlaylistAddCheck: VectorAsset by lazyMaterialIcon {
    group {
        path {
            moveTo(14.0f, 10.0f)
            lineTo(2.0f, 10.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(12.0f)
            verticalLineToRelative(-2.0f)
            close()
            moveTo(14.0f, 6.0f)
            lineTo(2.0f, 6.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(12.0f)
            lineTo(14.0f, 6.0f)
            close()
            moveTo(2.0f, 16.0f)
            horizontalLineToRelative(8.0f)
            verticalLineToRelative(-2.0f)
            lineTo(2.0f, 14.0f)
            verticalLineToRelative(2.0f)
            close()
            moveTo(21.5f, 11.5f)
            lineTo(23.0f, 13.0f)
            lineToRelative(-6.99f, 7.0f)
            lineToRelative(-4.51f, -4.5f)
            lineTo(13.0f, 14.0f)
            lineToRelative(3.01f, 3.0f)
            lineToRelative(5.49f, -5.5f)
            close()
        }
    }
}
