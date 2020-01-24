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

package com.ivianuu.essentials.material.icons.rounded

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.OfflineBolt: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.02f)
        curveToRelative(-5.51f, 0.0f, -9.98f, 4.47f, -9.98f, 9.98f)
        reflectiveCurveToRelative(4.47f, 9.98f, 9.98f, 9.98f)
        reflectiveCurveToRelative(9.98f, -4.47f, 9.98f, -9.98f)
        reflectiveCurveTo(17.51f, 2.02f, 12.0f, 2.02f)
        close()
        moveTo(11.48f, 17.88f)
        verticalLineToRelative(-4.14f)
        lineTo(8.82f, 13.74f)
        curveToRelative(-0.37f, 0.0f, -0.62f, -0.4f, -0.44f, -0.73f)
        lineToRelative(3.68f, -7.17f)
        curveToRelative(0.23f, -0.47f, 0.94f, -0.3f, 0.94f, 0.23f)
        verticalLineToRelative(4.19f)
        horizontalLineToRelative(2.54f)
        curveToRelative(0.37f, 0.0f, 0.61f, 0.39f, 0.45f, 0.72f)
        lineToRelative(-3.56f, 7.12f)
        curveToRelative(-0.24f, 0.48f, -0.95f, 0.31f, -0.95f, -0.22f)
        close()
    }
}
