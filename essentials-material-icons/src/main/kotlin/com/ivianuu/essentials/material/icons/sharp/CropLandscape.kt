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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Sharp.CropLandscape: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(21.0f, 5.0f)
        lineTo(3.0f, 5.0f)
        verticalLineToRelative(14.0f)
        horizontalLineToRelative(18.0f)
        lineTo(21.0f, 5.0f)
        close()
        moveTo(19.0f, 17.0f)
        lineTo(5.0f, 17.0f)
        lineTo(5.0f, 7.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(10.0f)
        close()
    }
}
