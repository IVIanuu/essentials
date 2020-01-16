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

val Icons.TwoTone.PhotoLibrary: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(8.0f, 16.0f)
        horizontalLineToRelative(12.0f)
        lineTo(20.0f, 4.0f)
        lineTo(8.0f, 4.0f)
        verticalLineToRelative(12.0f)
        close()
        moveTo(11.5f, 11.67f)
        lineToRelative(1.69f, 2.26f)
        lineToRelative(2.48f, -3.09f)
        lineTo(19.0f, 15.0f)
        lineTo(9.0f, 15.0f)
        lineToRelative(2.5f, -3.33f)
        close()
    }
    path {
        moveTo(22.0f, 16.0f)
        lineTo(22.0f, 4.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        lineTo(8.0f, 2.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        close()
        moveTo(20.0f, 16.0f)
        lineTo(8.0f, 16.0f)
        lineTo(8.0f, 4.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(12.0f)
        close()
        moveTo(15.67f, 10.83f)
        lineToRelative(-2.48f, 3.09f)
        lineToRelative(-1.69f, -2.25f)
        lineTo(9.0f, 15.0f)
        horizontalLineToRelative(10.0f)
        close()
        moveTo(4.0f, 22.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(-2.0f)
        lineTo(4.0f, 20.0f)
        lineTo(4.0f, 6.0f)
        lineTo(2.0f, 6.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        close()
    }
}
