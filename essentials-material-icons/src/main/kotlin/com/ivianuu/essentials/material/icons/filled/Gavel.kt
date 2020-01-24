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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.Gavel: VectorAsset by lazyMaterialIcon {
    group {
        path {
            moveTo(1.0f, 21.0f)
            horizontalLineToRelative(12.0f)
            verticalLineToRelative(2.0f)
            horizontalLineTo(1.0f)
            close()
            moveTo(5.245f, 8.07f)
            lineToRelative(2.83f, -2.827f)
            lineToRelative(14.14f, 14.142f)
            lineToRelative(-2.828f, 2.828f)
            close()
            moveTo(12.317f, 1.0f)
            lineToRelative(5.657f, 5.656f)
            lineToRelative(-2.83f, 2.83f)
            lineToRelative(-5.654f, -5.66f)
            close()
            moveTo(3.825f, 9.485f)
            lineToRelative(5.657f, 5.657f)
            lineToRelative(-2.828f, 2.828f)
            lineToRelative(-5.657f, -5.657f)
            close()
        }
    }
}
