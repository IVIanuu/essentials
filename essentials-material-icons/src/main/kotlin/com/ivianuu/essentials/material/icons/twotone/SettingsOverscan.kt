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

val Icons.TwoTone.SettingsOverscan: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(3.0f, 19.01f)
        horizontalLineToRelative(18.0f)
        lineTo(21.0f, 4.99f)
        lineTo(3.0f, 4.99f)
        verticalLineToRelative(14.02f)
        close()
        moveTo(18.0f, 10.0f)
        lineToRelative(2.5f, 2.01f)
        lineTo(18.0f, 14.0f)
        verticalLineToRelative(-4.0f)
        close()
        moveTo(12.01f, 5.5f)
        lineTo(14.0f, 8.0f)
        horizontalLineToRelative(-4.0f)
        lineToRelative(2.01f, -2.5f)
        close()
        moveTo(14.0f, 16.0f)
        lineToRelative(-1.99f, 2.5f)
        lineTo(10.0f, 16.0f)
        horizontalLineToRelative(4.0f)
        close()
        moveTo(6.0f, 10.0f)
        verticalLineToRelative(4.0f)
        lineToRelative(-2.5f, -1.99f)
        lineTo(6.0f, 10.0f)
        close()
    }
    path {
        moveTo(14.0f, 16.0f)
        horizontalLineToRelative(-4.0f)
        lineToRelative(2.01f, 2.5f)
        close()
        moveTo(18.0f, 10.0f)
        verticalLineToRelative(4.0f)
        lineToRelative(2.5f, -1.99f)
        close()
        moveTo(21.0f, 3.0f)
        lineTo(3.0f, 3.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(18.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(23.0f, 5.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(21.0f, 19.01f)
        lineTo(3.0f, 19.01f)
        lineTo(3.0f, 4.99f)
        horizontalLineToRelative(18.0f)
        verticalLineToRelative(14.02f)
        close()
        moveTo(6.0f, 10.0f)
        lineToRelative(-2.5f, 2.01f)
        lineTo(6.0f, 14.0f)
        close()
        moveTo(12.01f, 5.5f)
        lineTo(10.0f, 8.0f)
        horizontalLineToRelative(4.0f)
        close()
    }
}
