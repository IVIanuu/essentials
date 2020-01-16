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

import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group

val Icons.Filled.AirlineSeatFlat: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(22.0f, 11.0f)
        verticalLineToRelative(2.0f)
        lineTo(9.0f, 13.0f)
        lineTo(9.0f, 7.0f)
        horizontalLineToRelative(9.0f)
        curveToRelative(2.21f, 0.0f, 4.0f, 1.79f, 4.0f, 4.0f)
        close()
        moveTo(2.0f, 14.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(-2.0f)
        lineTo(2.0f, 14.0f)
        close()
        moveTo(7.14f, 12.1f)
        curveToRelative(1.16f, -1.19f, 1.14f, -3.08f, -0.04f, -4.24f)
        curveToRelative(-1.19f, -1.16f, -3.08f, -1.14f, -4.24f, 0.04f)
        curveToRelative(-1.16f, 1.19f, -1.14f, 3.08f, 0.04f, 4.24f)
        curveToRelative(1.19f, 1.16f, 3.08f, 1.14f, 4.24f, -0.04f)
        close()
    }
}
