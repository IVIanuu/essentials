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

val Icons.Filled.Highlight: VectorAsset by lazyMaterialIcon {
    group {
        path {
            moveTo(6.0f, 14.0f)
            lineToRelative(3.0f, 3.0f)
            verticalLineToRelative(5.0f)
            horizontalLineToRelative(6.0f)
            verticalLineToRelative(-5.0f)
            lineToRelative(3.0f, -3.0f)
            lineTo(18.0f, 9.0f)
            lineTo(6.0f, 9.0f)
            close()
            moveTo(11.0f, 2.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(3.0f)
            horizontalLineToRelative(-2.0f)
            close()
            moveTo(3.5f, 5.875f)
            lineTo(4.914f, 4.46f)
            lineToRelative(2.12f, 2.122f)
            lineTo(5.62f, 7.997f)
            close()
            moveTo(16.96f, 6.585f)
            lineToRelative(2.123f, -2.12f)
            lineToRelative(1.414f, 1.414f)
            lineTo(18.375f, 8.0f)
            close()
        }
    }
}
