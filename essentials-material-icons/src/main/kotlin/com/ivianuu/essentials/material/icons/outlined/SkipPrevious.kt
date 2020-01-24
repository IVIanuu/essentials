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

package com.ivianuu.essentials.material.icons.outlined

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Outlined.SkipPrevious: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(6.0f, 6.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(12.0f)
        lineTo(6.0f, 18.0f)
        close()
        moveTo(9.5f, 12.0f)
        lineToRelative(8.5f, 6.0f)
        lineTo(18.0f, 6.0f)
        lineToRelative(-8.5f, 6.0f)
        close()
        moveTo(16.0f, 14.14f)
        lineTo(12.97f, 12.0f)
        lineTo(16.0f, 9.86f)
        verticalLineToRelative(4.28f)
        close()
    }
}
