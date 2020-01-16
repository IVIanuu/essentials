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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Sharp.House: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(19.0f, 9.3f)
        verticalLineTo(4.0f)
        horizontalLineToRelative(-3.0f)
        verticalLineToRelative(2.6f)
        lineTo(12.0f, 3.0f)
        lineTo(2.0f, 12.0f)
        horizontalLineToRelative(3.0f)
        verticalLineToRelative(8.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(-6.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(-8.0f)
        horizontalLineToRelative(3.0f)
        lineTo(19.0f, 9.3f)
        close()
        moveTo(10.0f, 10.0f)
        curveToRelative(0.0f, -1.1f, 0.9f, -2.0f, 2.0f, -2.0f)
        reflectiveCurveToRelative(2.0f, 0.9f, 2.0f, 2.0f)
        horizontalLineTo(10.0f)
        close()
    }
}
