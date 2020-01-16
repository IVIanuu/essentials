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

val Icons.TwoTone.DynamicFeed: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f, strokeAlpha = 0.3f) {
        moveTo(12.0f, 7.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(-8.0f)
        close()
    }
    path {
        moveTo(8.0f, 8.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(7.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(9.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineTo(8.0f)
        verticalLineTo(8.0f)
        close()
    }
    path {
        moveTo(20.0f, 3.0f)
        horizontalLineToRelative(-8.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(6.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(8.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineTo(5.0f)
        curveTo(22.0f, 3.9f, 21.1f, 3.0f, 20.0f, 3.0f)
        close()
        moveTo(20.0f, 11.0f)
        horizontalLineToRelative(-8.0f)
        verticalLineTo(7.0f)
        horizontalLineToRelative(8.0f)
        verticalLineTo(11.0f)
        close()
    }
    path {
        moveTo(4.0f, 12.0f)
        horizontalLineTo(2.0f)
        verticalLineToRelative(7.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(9.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineTo(4.0f)
        verticalLineTo(12.0f)
        close()
    }
}
