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
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.Details: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(3.0f, 4.0f)
        lineToRelative(9.0f, 16.0f)
        lineToRelative(9.0f, -16.0f)
        lineTo(3.0f, 4.0f)
        close()
        moveTo(6.38f, 6.0f)
        horizontalLineToRelative(11.25f)
        lineTo(12.0f, 16.0f)
        lineTo(6.38f, 6.0f)
        close()
    }
}
