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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Filled.TabletMac: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.5f, 0.0f)
        horizontalLineToRelative(-14.0f)
        curveTo(3.12f, 0.0f, 2.0f, 1.12f, 2.0f, 2.5f)
        verticalLineToRelative(19.0f)
        curveTo(2.0f, 22.88f, 3.12f, 24.0f, 4.5f, 24.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(1.38f, 0.0f, 2.5f, -1.12f, 2.5f, -2.5f)
        verticalLineToRelative(-19.0f)
        curveTo(21.0f, 1.12f, 19.88f, 0.0f, 18.5f, 0.0f)
        close()
        moveTo(11.5f, 23.0f)
        curveToRelative(-0.83f, 0.0f, -1.5f, -0.67f, -1.5f, -1.5f)
        reflectiveCurveToRelative(0.67f, -1.5f, 1.5f, -1.5f)
        reflectiveCurveToRelative(1.5f, 0.67f, 1.5f, 1.5f)
        reflectiveCurveToRelative(-0.67f, 1.5f, -1.5f, 1.5f)
        close()
        moveTo(19.0f, 19.0f)
        lineTo(4.0f, 19.0f)
        lineTo(4.0f, 3.0f)
        horizontalLineToRelative(15.0f)
        verticalLineToRelative(16.0f)
        close()
    }
}
