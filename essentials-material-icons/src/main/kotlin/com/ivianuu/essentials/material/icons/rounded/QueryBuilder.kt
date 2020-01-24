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

val Icons.Rounded.QueryBuilder: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(11.99f, 2.0f)
        curveTo(6.47f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.47f, 10.0f, 9.99f, 10.0f)
        curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
        reflectiveCurveTo(17.52f, 2.0f, 11.99f, 2.0f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-4.42f, 0.0f, -8.0f, -3.58f, -8.0f, -8.0f)
        reflectiveCurveToRelative(3.58f, -8.0f, 8.0f, -8.0f)
        reflectiveCurveToRelative(8.0f, 3.58f, 8.0f, 8.0f)
        reflectiveCurveToRelative(-3.58f, 8.0f, -8.0f, 8.0f)
        close()
        moveTo(11.78f, 7.0f)
        horizontalLineToRelative(-0.06f)
        curveToRelative(-0.4f, 0.0f, -0.72f, 0.32f, -0.72f, 0.72f)
        verticalLineToRelative(4.72f)
        curveToRelative(0.0f, 0.35f, 0.18f, 0.68f, 0.49f, 0.86f)
        lineToRelative(4.15f, 2.49f)
        curveToRelative(0.34f, 0.2f, 0.78f, 0.1f, 0.98f, -0.24f)
        curveToRelative(0.21f, -0.34f, 0.1f, -0.79f, -0.25f, -0.99f)
        lineToRelative(-3.87f, -2.3f)
        lineTo(12.5f, 7.72f)
        curveToRelative(0.0f, -0.4f, -0.32f, -0.72f, -0.72f, -0.72f)
        close()
    }
}
