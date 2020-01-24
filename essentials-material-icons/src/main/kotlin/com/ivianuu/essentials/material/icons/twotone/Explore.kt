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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.Explore: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 4.0f)
        curveToRelative(-4.41f, 0.0f, -8.0f, 3.59f, -8.0f, 8.0f)
        reflectiveCurveToRelative(3.59f, 8.0f, 8.0f, 8.0f)
        reflectiveCurveToRelative(8.0f, -3.59f, 8.0f, -8.0f)
        reflectiveCurveToRelative(-3.59f, -8.0f, -8.0f, -8.0f)
        close()
        moveTo(14.01f, 14.01f)
        lineTo(6.5f, 17.5f)
        lineToRelative(3.49f, -7.51f)
        lineTo(17.5f, 6.5f)
        lineToRelative(-3.49f, 7.51f)
        close()
    }
    path {
        moveTo(12.0f, 2.0f)
        curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-4.41f, 0.0f, -8.0f, -3.59f, -8.0f, -8.0f)
        reflectiveCurveToRelative(3.59f, -8.0f, 8.0f, -8.0f)
        reflectiveCurveToRelative(8.0f, 3.59f, 8.0f, 8.0f)
        reflectiveCurveToRelative(-3.59f, 8.0f, -8.0f, 8.0f)
        close()
        moveTo(6.5f, 17.5f)
        lineToRelative(7.51f, -3.49f)
        lineTo(17.5f, 6.5f)
        lineTo(9.99f, 9.99f)
        lineTo(6.5f, 17.5f)
        close()
        moveTo(12.0f, 10.9f)
        curveToRelative(0.61f, 0.0f, 1.1f, 0.49f, 1.1f, 1.1f)
        reflectiveCurveToRelative(-0.49f, 1.1f, -1.1f, 1.1f)
        reflectiveCurveToRelative(-1.1f, -0.49f, -1.1f, -1.1f)
        reflectiveCurveToRelative(0.49f, -1.1f, 1.1f, -1.1f)
        close()
    }
}
