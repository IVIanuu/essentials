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

val Icons.TwoTone.TvOff: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(3.0f, 19.0f)
        horizontalLineToRelative(13.46f)
        lineToRelative(-12.0f, -12.0f)
        lineTo(3.0f, 7.0f)
        close()
        moveTo(10.12f, 7.0f)
        lineTo(21.0f, 17.88f)
        lineTo(21.0f, 7.0f)
        close()
    }
    path {
        moveTo(21.0f, 7.0f)
        verticalLineToRelative(10.88f)
        lineToRelative(1.85f, 1.85f)
        curveToRelative(0.09f, -0.23f, 0.15f, -0.47f, 0.15f, -0.73f)
        lineTo(23.0f, 7.0f)
        curveToRelative(0.0f, -1.11f, -0.89f, -2.0f, -2.0f, -2.0f)
        horizontalLineToRelative(-7.58f)
        lineToRelative(3.29f, -3.3f)
        lineTo(16.0f, 1.0f)
        lineToRelative(-4.0f, 4.0f)
        lineToRelative(-4.0f, -4.0f)
        lineToRelative(-0.7f, 0.7f)
        lineTo(10.58f, 5.0f)
        lineTo(8.12f, 5.0f)
        lineToRelative(2.0f, 2.0f)
        lineTo(21.0f, 7.0f)
        close()
        moveTo(20.46f, 23.0f)
        lineToRelative(1.26f, -1.27f)
        lineToRelative(-1.26f, 1.26f)
        close()
        moveTo(2.41f, 2.13f)
        lineToRelative(-0.14f, 0.14f)
        lineTo(1.0f, 3.54f)
        lineToRelative(1.53f, 1.53f)
        curveTo(1.65f, 5.28f, 1.0f, 6.06f, 1.0f, 7.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(15.46f)
        lineToRelative(1.99f, 1.99f)
        lineToRelative(1.26f, -1.26f)
        lineToRelative(0.15f, -0.15f)
        lineTo(2.41f, 2.13f)
        close()
        moveTo(3.0f, 19.0f)
        lineTo(3.0f, 7.0f)
        horizontalLineToRelative(1.46f)
        lineToRelative(12.0f, 12.0f)
        lineTo(3.0f, 19.0f)
        close()
    }
}
