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

val Icons.Filled.BatteryAlert: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(15.67f, 4.0f)
        lineTo(14.0f, 4.0f)
        lineTo(14.0f, 2.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(2.0f)
        lineTo(8.33f, 4.0f)
        curveTo(7.6f, 4.0f, 7.0f, 4.6f, 7.0f, 5.33f)
        verticalLineToRelative(15.33f)
        curveTo(7.0f, 21.4f, 7.6f, 22.0f, 8.33f, 22.0f)
        horizontalLineToRelative(7.33f)
        curveToRelative(0.74f, 0.0f, 1.34f, -0.6f, 1.34f, -1.33f)
        lineTo(17.0f, 5.33f)
        curveTo(17.0f, 4.6f, 16.4f, 4.0f, 15.67f, 4.0f)
        close()
        moveTo(13.0f, 18.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(13.0f, 14.0f)
        horizontalLineToRelative(-2.0f)
        lineTo(11.0f, 9.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(5.0f)
        close()
    }
}
