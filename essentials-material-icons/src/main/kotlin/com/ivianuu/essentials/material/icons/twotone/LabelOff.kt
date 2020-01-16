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

val Icons.TwoTone.LabelOff: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(5.0f, 7.03f)
        verticalLineTo(17.0f)
        horizontalLineToRelative(9.97f)
        close()
        moveTo(16.0f, 7.0f)
        horizontalLineToRelative(-5.37f)
        lineToRelative(7.29f, 7.29f)
        lineTo(19.55f, 12.0f)
        close()
    }
    path {
        moveTo(16.0f, 7.0f)
        lineToRelative(3.55f, 5.0f)
        lineToRelative(-1.63f, 2.29f)
        lineToRelative(1.43f, 1.43f)
        lineTo(22.0f, 12.0f)
        lineToRelative(-4.37f, -6.16f)
        curveTo(17.27f, 5.33f, 16.67f, 5.0f, 16.0f, 5.0f)
        lineToRelative(-7.37f, 0.01f)
        lineToRelative(2.0f, 1.99f)
        lineTo(16.0f, 7.0f)
        close()
        moveTo(2.0f, 4.03f)
        lineToRelative(1.58f, 1.58f)
        curveTo(3.22f, 5.96f, 3.0f, 6.46f, 3.0f, 7.0f)
        verticalLineToRelative(10.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 1.99f, 2.0f, 1.99f)
        lineTo(16.0f, 19.0f)
        curveToRelative(0.28f, 0.0f, 0.55f, -0.07f, 0.79f, -0.18f)
        lineTo(18.97f, 21.0f)
        lineToRelative(1.41f, -1.41f)
        lineTo(3.41f, 2.62f)
        lineTo(2.0f, 4.03f)
        close()
        moveTo(5.0f, 7.03f)
        lineTo(14.97f, 17.0f)
        lineTo(5.0f, 17.0f)
        lineTo(5.0f, 7.03f)
        close()
    }
}
