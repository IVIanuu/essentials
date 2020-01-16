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

val Icons.TwoTone.SignalCellularNoSim: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(10.83f, 5.0f)
        lineTo(9.36f, 6.47f)
        lineTo(17.0f, 14.11f)
        verticalLineTo(5.0f)
        close()
        moveTo(7.0f, 9.79f)
        verticalLineTo(19.0f)
        horizontalLineToRelative(9.23f)
        close()
    }
    path {
        moveTo(10.83f, 5.0f)
        lineTo(17.0f, 5.0f)
        verticalLineToRelative(9.11f)
        lineToRelative(2.0f, 2.0f)
        lineTo(19.0f, 5.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        horizontalLineToRelative(-7.0f)
        lineTo(7.94f, 5.06f)
        lineToRelative(1.42f, 1.42f)
        lineTo(10.83f, 5.0f)
        close()
        moveTo(21.26f, 21.21f)
        lineTo(3.79f, 3.74f)
        lineTo(2.38f, 5.15f)
        lineTo(5.0f, 7.77f)
        lineTo(5.0f, 19.0f)
        curveToRelative(0.0f, 1.11f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(11.23f)
        lineToRelative(1.62f, 1.62f)
        lineToRelative(1.41f, -1.41f)
        close()
        moveTo(7.0f, 19.0f)
        lineTo(7.0f, 9.79f)
        lineTo(16.23f, 19.0f)
        lineTo(7.0f, 19.0f)
        close()
    }
}
