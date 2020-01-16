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

val Icons.TwoTone.Filter: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(7.0f, 17.0f)
        horizontalLineToRelative(14.0f)
        lineTo(21.0f, 3.0f)
        lineTo(7.0f, 3.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(11.25f, 11.47f)
        lineToRelative(1.96f, 2.36f)
        lineToRelative(2.75f, -3.54f)
        lineTo(19.5f, 15.0f)
        horizontalLineToRelative(-11.0f)
        lineToRelative(2.75f, -3.53f)
        close()
    }
    path {
        moveTo(1.0f, 21.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(-2.0f)
        lineTo(3.0f, 21.0f)
        lineTo(3.0f, 5.0f)
        lineTo(1.0f, 5.0f)
        verticalLineToRelative(16.0f)
        close()
        moveTo(21.0f, 1.0f)
        lineTo(7.0f, 1.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(23.0f, 3.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(21.0f, 17.0f)
        lineTo(7.0f, 17.0f)
        lineTo(7.0f, 3.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(15.96f, 10.29f)
        lineToRelative(-2.75f, 3.54f)
        lineToRelative(-1.96f, -2.36f)
        lineTo(8.5f, 15.0f)
        horizontalLineToRelative(11.0f)
        close()
    }
}
