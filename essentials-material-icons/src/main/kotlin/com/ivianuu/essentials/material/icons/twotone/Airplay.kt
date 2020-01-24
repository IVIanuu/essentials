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

import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path
import androidx.ui.graphics.vector.VectorAsset

val Icons.TwoTone.Airplay: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(6.0f, 22.0f)
        lineToRelative(12.0f, 0.0f)
        lineToRelative(-6.0f, -6.0f)
        close()
    }
    path {
        moveTo(21.0f, 3.0f)
        horizontalLineTo(3.0f)
        curveTo(1.9f, 3.0f, 1.0f, 3.9f, 1.0f, 5.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineTo(3.0f)
        verticalLineTo(5.0f)
        horizontalLineToRelative(18.0f)
        verticalLineToRelative(12.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(4.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineTo(5.0f)
        curveTo(23.0f, 3.9f, 22.1f, 3.0f, 21.0f, 3.0f)
        close()
    }
}
