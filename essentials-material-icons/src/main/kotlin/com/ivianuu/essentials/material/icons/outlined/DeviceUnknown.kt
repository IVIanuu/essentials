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

package com.ivianuu.essentials.material.icons.outlined

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Outlined.DeviceUnknown: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(17.0f, 1.0f)
        lineTo(7.0f, 1.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(18.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(19.0f, 3.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(17.0f, 19.0f)
        lineTo(7.0f, 19.0f)
        lineTo(7.0f, 5.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(12.0f, 6.72f)
        curveToRelative(-1.96f, 0.0f, -3.5f, 1.52f, -3.5f, 3.47f)
        horizontalLineToRelative(1.75f)
        curveToRelative(0.0f, -0.93f, 0.82f, -1.75f, 1.75f, -1.75f)
        reflectiveCurveToRelative(1.75f, 0.82f, 1.75f, 1.75f)
        curveToRelative(0.0f, 1.75f, -2.63f, 1.57f, -2.63f, 4.45f)
        horizontalLineToRelative(1.76f)
        curveToRelative(0.0f, -1.96f, 2.62f, -2.19f, 2.62f, -4.45f)
        curveToRelative(0.0f, -1.96f, -1.54f, -3.47f, -3.5f, -3.47f)
        close()
        moveTo(11.0f, 16.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-2.0f)
        close()
    }
}
