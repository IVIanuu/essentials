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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.NoSim: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(7.0f, 19.0f)
        horizontalLineToRelative(9.23f)
        lineTo(7.0f, 9.77f)
        close()
    }
    path {
        moveTo(3.79f, 3.74f)
        lineTo(2.38f, 5.15f)
        lineToRelative(2.74f, 2.74f)
        lineToRelative(-0.12f, 0.12f)
        verticalLineTo(19.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(0.35f, 0.0f, 0.68f, -0.1f, 0.97f, -0.26f)
        lineToRelative(1.88f, 1.88f)
        lineToRelative(1.41f, -1.41f)
        lineTo(3.79f, 3.74f)
        close()
        moveTo(7.0f, 19.0f)
        verticalLineTo(9.77f)
        lineTo(16.23f, 19.0f)
        horizontalLineTo(7.0f)
        close()
    }
    path(fillAlpha = 0.3f) {
        moveTo(10.84f, 5.0f)
        lineTo(9.36f, 6.47f)
        lineTo(17.0f, 14.11f)
        verticalLineTo(5.0f)
        close()
    }
    path {
        moveTo(10.84f, 5.0f)
        horizontalLineTo(17.0f)
        verticalLineToRelative(9.11f)
        lineToRelative(2.0f, 2.0f)
        verticalLineTo(5.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        horizontalLineToRelative(-6.99f)
        lineTo(7.95f, 5.06f)
        lineToRelative(1.41f, 1.41f)
        lineTo(10.84f, 5.0f)
        close()
    }
}
