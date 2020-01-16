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

val Icons.Outlined.SettingsCell: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(7.0f, 22.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        lineTo(7.0f, 24.0f)
        close()
        moveTo(11.0f, 22.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(15.0f, 22.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(16.0f, 0.01f)
        lineTo(8.0f, 0.0f)
        curveTo(6.9f, 0.0f, 6.0f, 0.9f, 6.0f, 2.0f)
        verticalLineToRelative(16.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(8.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(18.0f, 2.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -1.99f, -2.0f, -1.99f)
        close()
        moveTo(16.0f, 18.0f)
        lineTo(8.0f, 18.0f)
        verticalLineToRelative(-1.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(1.0f)
        close()
        moveTo(16.0f, 15.0f)
        lineTo(8.0f, 15.0f)
        lineTo(8.0f, 5.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(10.0f)
        close()
        moveTo(16.0f, 3.0f)
        lineTo(8.0f, 3.0f)
        lineTo(8.0f, 2.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(1.0f)
        close()
    }
}
