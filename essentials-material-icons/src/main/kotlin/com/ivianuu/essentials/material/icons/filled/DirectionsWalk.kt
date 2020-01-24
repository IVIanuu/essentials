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

package com.ivianuu.essentials.material.icons.filled

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.DirectionsWalk: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(13.5f, 5.5f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        reflectiveCurveToRelative(-0.9f, -2.0f, -2.0f, -2.0f)
        reflectiveCurveToRelative(-2.0f, 0.9f, -2.0f, 2.0f)
        reflectiveCurveToRelative(0.9f, 2.0f, 2.0f, 2.0f)
        close()
        moveTo(9.8f, 8.9f)
        lineTo(7.0f, 23.0f)
        horizontalLineToRelative(2.1f)
        lineToRelative(1.8f, -8.0f)
        lineToRelative(2.1f, 2.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-7.5f)
        lineToRelative(-2.1f, -2.0f)
        lineToRelative(0.6f, -3.0f)
        curveTo(14.8f, 12.0f, 16.8f, 13.0f, 19.0f, 13.0f)
        verticalLineToRelative(-2.0f)
        curveToRelative(-1.9f, 0.0f, -3.5f, -1.0f, -4.3f, -2.4f)
        lineToRelative(-1.0f, -1.6f)
        curveToRelative(-0.4f, -0.6f, -1.0f, -1.0f, -1.7f, -1.0f)
        curveToRelative(-0.3f, 0.0f, -0.5f, 0.1f, -0.8f, 0.1f)
        lineTo(6.0f, 8.3f)
        verticalLineTo(13.0f)
        horizontalLineToRelative(2.0f)
        verticalLineTo(9.6f)
        lineToRelative(1.8f, -0.7f)
    }
}
