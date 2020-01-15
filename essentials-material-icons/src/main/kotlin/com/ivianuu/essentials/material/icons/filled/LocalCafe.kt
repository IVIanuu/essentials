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

val Icons.Filled.LocalCafe: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.0f, 3.0f)
        lineTo(4.0f, 3.0f)
        verticalLineToRelative(10.0f)
        curveToRelative(0.0f, 2.21f, 1.79f, 4.0f, 4.0f, 4.0f)
        horizontalLineToRelative(6.0f)
        curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
        verticalLineToRelative(-3.0f)
        horizontalLineToRelative(2.0f)
        curveToRelative(1.11f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(22.0f, 5.0f)
        curveToRelative(0.0f, -1.11f, -0.89f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(20.0f, 8.0f)
        horizontalLineToRelative(-2.0f)
        lineTo(18.0f, 5.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(3.0f)
        close()
        moveTo(4.0f, 19.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(2.0f)
        lineTo(4.0f, 21.0f)
        close()
    }
}
