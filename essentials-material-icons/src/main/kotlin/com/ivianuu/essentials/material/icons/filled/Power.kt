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
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.Power: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(16.01f, 7.0f)
        lineTo(16.0f, 3.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineTo(3.0f)
        horizontalLineTo(8.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(-0.01f)
        curveTo(7.0f, 6.99f, 6.0f, 7.99f, 6.0f, 8.99f)
        verticalLineToRelative(5.49f)
        lineTo(9.5f, 18.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(-3.0f)
        lineToRelative(3.5f, -3.51f)
        verticalLineToRelative(-5.5f)
        curveToRelative(0.0f, -1.0f, -1.0f, -2.0f, -1.99f, -1.99f)
        close()
    }
}
