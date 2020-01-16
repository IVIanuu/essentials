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

val Icons.TwoTone.FontDownload: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(4.0f, 20.0f)
        horizontalLineToRelative(16.0f)
        lineTo(20.0f, 4.0f)
        lineTo(4.0f, 4.0f)
        verticalLineToRelative(16.0f)
        close()
        moveTo(11.07f, 5.5f)
        horizontalLineToRelative(1.86f)
        lineToRelative(5.11f, 13.0f)
        horizontalLineToRelative(-2.09f)
        lineToRelative(-1.14f, -3.0f)
        lineTo(9.17f, 15.5f)
        lineToRelative(-1.12f, 3.0f)
        lineTo(5.96f, 18.5f)
        lineToRelative(5.11f, -13.0f)
        close()
        moveTo(12.0f, 7.98f)
        lineTo(9.93f, 13.5f)
        horizontalLineToRelative(4.14f)
        close()
    }
    path {
        moveTo(9.17f, 15.5f)
        horizontalLineToRelative(5.64f)
        lineToRelative(1.14f, 3.0f)
        horizontalLineToRelative(2.09f)
        lineToRelative(-5.11f, -13.0f)
        horizontalLineToRelative(-1.86f)
        lineToRelative(-5.11f, 13.0f)
        horizontalLineToRelative(2.09f)
        lineToRelative(1.12f, -3.0f)
        close()
        moveTo(12.0f, 7.98f)
        lineToRelative(2.07f, 5.52f)
        lineTo(9.93f, 13.5f)
        lineTo(12.0f, 7.98f)
        close()
        moveTo(20.0f, 2.0f)
        lineTo(4.0f, 2.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(16.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(22.0f, 4.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(20.0f, 20.0f)
        lineTo(4.0f, 20.0f)
        lineTo(4.0f, 4.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(16.0f)
        close()
    }
}
