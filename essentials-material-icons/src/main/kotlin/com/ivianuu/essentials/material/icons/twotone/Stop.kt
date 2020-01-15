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

package com.ivianuu.essentials.material.icons.twotone

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.Stop: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(8.0f, 8.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(8.0f)
        horizontalLineTo(8.0f)
        close()
    }
    path {
        moveTo(6.0f, 18.0f)
        horizontalLineToRelative(12.0f)
        verticalLineTo(6.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(12.0f)
        close()
        moveTo(8.0f, 8.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(8.0f)
        horizontalLineTo(8.0f)
        verticalLineTo(8.0f)
        close()
    }
}
