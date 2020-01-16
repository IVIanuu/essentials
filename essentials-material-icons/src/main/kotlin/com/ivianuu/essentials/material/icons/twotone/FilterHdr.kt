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

val Icons.TwoTone.FilterHdr: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(5.0f, 16.0f)
        horizontalLineToRelative(3.04f)
        lineToRelative(-1.52f, -2.03f)
        close()
    }
    path {
        moveTo(9.78f, 11.63f)
        lineToRelative(1.25f, 1.67f)
        lineTo(14.0f, 9.33f)
        lineTo(19.0f, 16.0f)
        horizontalLineToRelative(-8.46f)
        lineToRelative(-4.01f, -5.37f)
        lineTo(1.0f, 18.0f)
        horizontalLineToRelative(22.0f)
        lineTo(14.0f, 6.0f)
        lineToRelative(-4.22f, 5.63f)
        close()
        moveTo(5.0f, 16.0f)
        lineToRelative(1.52f, -2.03f)
        lineTo(8.04f, 16.0f)
        horizontalLineTo(5.0f)
        close()
    }
}
