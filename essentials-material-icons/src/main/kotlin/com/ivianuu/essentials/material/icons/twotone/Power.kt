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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.TwoTone.Power: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(8.0f, 13.65f)
        lineToRelative(3.5f, 3.52f)
        verticalLineTo(19.0f)
        horizontalLineToRelative(1.0f)
        verticalLineToRelative(-1.83f)
        lineToRelative(3.5f, -3.51f)
        verticalLineTo(9.0f)
        horizontalLineTo(8.0f)
        close()
    }
    path {
        moveTo(16.0f, 7.0f)
        lineTo(16.0f, 3.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(-4.0f)
        lineTo(10.0f, 3.0f)
        lineTo(8.0f, 3.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(-0.01f)
        curveTo(6.89f, 7.0f, 6.0f, 7.89f, 6.0f, 8.98f)
        verticalLineToRelative(5.52f)
        lineTo(9.5f, 18.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(-3.0f)
        lineToRelative(3.5f, -3.5f)
        lineTo(18.0f, 9.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(16.0f, 13.66f)
        lineToRelative(-3.5f, 3.51f)
        lineTo(12.5f, 19.0f)
        horizontalLineToRelative(-1.0f)
        verticalLineToRelative(-1.83f)
        lineTo(8.0f, 13.65f)
        lineTo(8.0f, 9.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(4.66f)
        close()
    }
}
