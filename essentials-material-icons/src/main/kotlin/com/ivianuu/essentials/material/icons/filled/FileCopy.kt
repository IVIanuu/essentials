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

val Icons.Filled.FileCopy: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(16.0f, 1.0f)
        lineTo(4.0f, 1.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(14.0f)
        horizontalLineToRelative(2.0f)
        lineTo(4.0f, 3.0f)
        horizontalLineToRelative(12.0f)
        lineTo(16.0f, 1.0f)
        close()
        moveTo(15.0f, 5.0f)
        lineToRelative(6.0f, 6.0f)
        verticalLineToRelative(10.0f)
        curveToRelative(0.0f, 1.1f, -0.9f, 2.0f, -2.0f, 2.0f)
        lineTo(7.99f, 23.0f)
        curveTo(6.89f, 23.0f, 6.0f, 22.1f, 6.0f, 21.0f)
        lineToRelative(0.01f, -14.0f)
        curveToRelative(0.0f, -1.1f, 0.89f, -2.0f, 1.99f, -2.0f)
        horizontalLineToRelative(7.0f)
        close()
        moveTo(14.0f, 12.0f)
        horizontalLineToRelative(5.5f)
        lineTo(14.0f, 6.5f)
        lineTo(14.0f, 12.0f)
        close()
    }
}
