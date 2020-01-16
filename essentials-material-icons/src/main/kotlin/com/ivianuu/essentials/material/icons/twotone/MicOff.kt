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

val Icons.TwoTone.MicOff: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 3.7f)
        curveToRelative(-0.66f, 0.0f, -1.2f, 0.54f, -1.2f, 1.2f)
        verticalLineToRelative(1.51f)
        lineToRelative(2.39f, 2.39f)
        lineToRelative(0.01f, -3.9f)
        curveToRelative(0.0f, -0.66f, -0.54f, -1.2f, -1.2f, -1.2f)
        close()
    }
    path {
        moveTo(19.0f, 11.0f)
        horizontalLineToRelative(-1.7f)
        curveToRelative(0.0f, 0.58f, -0.1f, 1.13f, -0.27f, 1.64f)
        lineToRelative(1.27f, 1.27f)
        curveToRelative(0.44f, -0.88f, 0.7f, -1.87f, 0.7f, -2.91f)
        close()
        moveTo(4.41f, 2.86f)
        lineTo(3.0f, 4.27f)
        lineToRelative(6.0f, 6.0f)
        verticalLineTo(11.0f)
        curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
        curveToRelative(0.23f, 0.0f, 0.44f, -0.03f, 0.65f, -0.08f)
        lineToRelative(1.66f, 1.66f)
        curveToRelative(-0.71f, 0.33f, -1.5f, 0.52f, -2.31f, 0.52f)
        curveToRelative(-2.76f, 0.0f, -5.3f, -2.1f, -5.3f, -5.1f)
        horizontalLineTo(5.0f)
        curveToRelative(0.0f, 3.41f, 2.72f, 6.23f, 6.0f, 6.72f)
        verticalLineTo(21.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-3.28f)
        curveToRelative(0.91f, -0.13f, 1.77f, -0.45f, 2.55f, -0.9f)
        lineToRelative(4.2f, 4.2f)
        lineToRelative(1.41f, -1.41f)
        lineTo(4.41f, 2.86f)
        close()
        moveTo(10.8f, 4.9f)
        curveToRelative(0.0f, -0.66f, 0.54f, -1.2f, 1.2f, -1.2f)
        reflectiveCurveToRelative(1.2f, 0.54f, 1.2f, 1.2f)
        lineToRelative(-0.01f, 3.91f)
        lineTo(15.0f, 10.6f)
        verticalLineTo(5.0f)
        curveToRelative(0.0f, -1.66f, -1.34f, -3.0f, -3.0f, -3.0f)
        curveToRelative(-1.54f, 0.0f, -2.79f, 1.16f, -2.96f, 2.65f)
        lineToRelative(1.76f, 1.76f)
        verticalLineTo(4.9f)
        close()
    }
}
