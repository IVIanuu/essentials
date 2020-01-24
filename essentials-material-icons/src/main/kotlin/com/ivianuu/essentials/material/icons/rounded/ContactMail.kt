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

val Icons.Rounded.ContactMail: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(21.0f, 8.0f)
        lineTo(21.0f, 7.0f)
        lineToRelative(-3.0f, 2.0f)
        lineToRelative(-3.0f, -2.0f)
        verticalLineToRelative(1.0f)
        lineToRelative(2.72f, 1.82f)
        curveToRelative(0.17f, 0.11f, 0.39f, 0.11f, 0.55f, 0.0f)
        lineTo(21.0f, 8.0f)
        close()
        moveTo(22.0f, 3.0f)
        lineTo(2.0f, 3.0f)
        curveTo(0.9f, 3.0f, 0.0f, 3.9f, 0.0f, 5.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(20.0f)
        curveToRelative(1.1f, 0.0f, 1.99f, -0.9f, 1.99f, -2.0f)
        lineTo(24.0f, 5.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(8.0f, 6.0f)
        curveToRelative(1.66f, 0.0f, 3.0f, 1.34f, 3.0f, 3.0f)
        reflectiveCurveToRelative(-1.34f, 3.0f, -3.0f, 3.0f)
        reflectiveCurveToRelative(-3.0f, -1.34f, -3.0f, -3.0f)
        reflectiveCurveToRelative(1.34f, -3.0f, 3.0f, -3.0f)
        close()
        moveTo(14.0f, 18.0f)
        lineTo(2.0f, 18.0f)
        verticalLineToRelative(-1.0f)
        curveToRelative(0.0f, -2.0f, 4.0f, -3.1f, 6.0f, -3.1f)
        reflectiveCurveToRelative(6.0f, 1.1f, 6.0f, 3.1f)
        verticalLineToRelative(1.0f)
        close()
        moveTo(21.5f, 12.0f)
        horizontalLineToRelative(-7.0f)
        curveToRelative(-0.28f, 0.0f, -0.5f, -0.22f, -0.5f, -0.5f)
        verticalLineToRelative(-5.0f)
        curveToRelative(0.0f, -0.28f, 0.22f, -0.5f, 0.5f, -0.5f)
        horizontalLineToRelative(7.0f)
        curveToRelative(0.28f, 0.0f, 0.5f, 0.22f, 0.5f, 0.5f)
        verticalLineToRelative(5.0f)
        curveToRelative(0.0f, 0.28f, -0.22f, 0.5f, -0.5f, 0.5f)
        close()
    }
}
