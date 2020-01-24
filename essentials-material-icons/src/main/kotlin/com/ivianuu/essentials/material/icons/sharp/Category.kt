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

val Icons.Sharp.Category: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.0f)
        lineToRelative(-5.5f, 9.0f)
        horizontalLineToRelative(11.0f)
        close()
    }
    path {
        moveTo(17.5f, 17.5f)
        moveToRelative(-4.5f, 0.0f)
        arcToRelative(4.5f, 4.5f, 0.0f, true, true, 9.0f, 0.0f)
        arcToRelative(4.5f, 4.5f, 0.0f, true, true, -9.0f, 0.0f)
    }
    path {
        moveTo(3.0f, 13.5f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(8.0f)
        horizontalLineTo(3.0f)
        close()
    }
}
