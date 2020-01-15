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

val Icons.Filled.ZoomOutMap: VectorAsset by lazyMaterialIcon {
    group {
        path {
            moveTo(15.0f, 3.0f)
            lineToRelative(2.3f, 2.3f)
            lineToRelative(-2.89f, 2.87f)
            lineToRelative(1.42f, 1.42f)
            lineTo(18.7f, 6.7f)
            lineTo(21.0f, 9.0f)
            lineTo(21.0f, 3.0f)
            close()
            moveTo(3.0f, 9.0f)
            lineToRelative(2.3f, -2.3f)
            lineToRelative(2.87f, 2.89f)
            lineToRelative(1.42f, -1.42f)
            lineTo(6.7f, 5.3f)
            lineTo(9.0f, 3.0f)
            lineTo(3.0f, 3.0f)
            close()
            moveTo(9.0f, 21.0f)
            lineToRelative(-2.3f, -2.3f)
            lineToRelative(2.89f, -2.87f)
            lineToRelative(-1.42f, -1.42f)
            lineTo(5.3f, 17.3f)
            lineTo(3.0f, 15.0f)
            verticalLineToRelative(6.0f)
            close()
            moveTo(21.0f, 15.0f)
            lineToRelative(-2.3f, 2.3f)
            lineToRelative(-2.87f, -2.89f)
            lineToRelative(-1.42f, 1.42f)
            lineToRelative(2.89f, 2.87f)
            lineTo(15.0f, 21.0f)
            horizontalLineToRelative(6.0f)
            close()
        }
    }
    group {
    }
}
