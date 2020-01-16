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

package com.ivianuu.essentials.material.icons.outlined

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Outlined.Web: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.0f, 4.0f)
        horizontalLineTo(4.0f)
        curveTo(2.9f, 4.0f, 2.01f, 4.9f, 2.01f, 6.0f)
        lineTo(2.0f, 18.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineTo(6.0f)
        curveTo(22.0f, 4.9f, 21.1f, 4.0f, 20.0f, 4.0f)
        close()
        moveTo(4.0f, 9.0f)
        horizontalLineToRelative(10.5f)
        verticalLineToRelative(3.5f)
        horizontalLineTo(4.0f)
        verticalLineTo(9.0f)
        close()
        moveTo(4.0f, 14.5f)
        horizontalLineToRelative(10.5f)
        verticalLineTo(18.0f)
        lineTo(4.0f, 18.0f)
        verticalLineTo(14.5f)
        close()
        moveTo(20.0f, 18.0f)
        lineToRelative(-3.5f, 0.0f)
        verticalLineTo(9.0f)
        horizontalLineTo(20.0f)
        verticalLineTo(18.0f)
        close()
    }
}
