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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Sharp.Directions: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(22.41f, 12.0f)
        lineTo(12.0f, 1.59f)
        lineTo(1.59f, 11.99f)
        lineTo(12.0f, 22.41f)
        lineTo(22.41f, 12.0f)
        close()
        moveTo(14.0f, 14.5f)
        verticalLineTo(12.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(3.0f)
        horizontalLineTo(8.0f)
        verticalLineToRelative(-5.0f)
        horizontalLineToRelative(6.0f)
        verticalLineTo(7.5f)
        lineToRelative(3.5f, 3.5f)
        lineToRelative(-3.5f, 3.5f)
        close()
    }
}
