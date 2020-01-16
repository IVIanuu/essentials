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

val Icons.TwoTone.WatchLater: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f, strokeAlpha = 0.3f) {
        moveTo(12.0f, 4.0f)
        curveToRelative(-4.41f, 0.0f, -8.0f, 3.59f, -8.0f, 8.0f)
        reflectiveCurveToRelative(3.59f, 8.0f, 8.0f, 8.0f)
        reflectiveCurveToRelative(8.0f, -3.59f, 8.0f, -8.0f)
        reflectiveCurveToRelative(-3.59f, -8.0f, -8.0f, -8.0f)
        close()
        moveTo(16.2f, 16.2f)
        lineTo(11.0f, 13.0f)
        lineTo(11.0f, 7.0f)
        horizontalLineToRelative(1.5f)
        verticalLineToRelative(5.2f)
        lineToRelative(4.5f, 2.7f)
        lineToRelative(-0.8f, 1.3f)
        close()
    }
    path {
        moveTo(12.0f, 2.0f)
        curveTo(6.5f, 2.0f, 2.0f, 6.5f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.5f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.5f, 10.0f, -10.0f)
        reflectiveCurveTo(17.5f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-4.41f, 0.0f, -8.0f, -3.59f, -8.0f, -8.0f)
        reflectiveCurveToRelative(3.59f, -8.0f, 8.0f, -8.0f)
        reflectiveCurveToRelative(8.0f, 3.59f, 8.0f, 8.0f)
        reflectiveCurveToRelative(-3.59f, 8.0f, -8.0f, 8.0f)
        close()
        moveTo(12.5f, 7.0f)
        lineTo(11.0f, 7.0f)
        verticalLineToRelative(6.0f)
        lineToRelative(5.2f, 3.2f)
        lineToRelative(0.8f, -1.3f)
        lineToRelative(-4.5f, -2.7f)
        close()
    }
}
