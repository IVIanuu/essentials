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

val Icons.Rounded.PregnantWoman: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(9.0f, 4.0f)
        curveToRelative(0.0f, -1.11f, 0.89f, -2.0f, 2.0f, -2.0f)
        reflectiveCurveToRelative(2.0f, 0.89f, 2.0f, 2.0f)
        reflectiveCurveToRelative(-0.89f, 2.0f, -2.0f, 2.0f)
        reflectiveCurveToRelative(-2.0f, -0.89f, -2.0f, -2.0f)
        close()
        moveTo(16.0f, 13.0f)
        curveToRelative(-0.01f, -1.34f, -0.83f, -2.51f, -2.0f, -3.0f)
        curveToRelative(0.0f, -1.71f, -1.42f, -3.08f, -3.16f, -3.0f)
        curveTo(9.22f, 7.09f, 8.0f, 8.54f, 8.0f, 10.16f)
        lineTo(8.0f, 16.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(1.0f)
        verticalLineToRelative(3.5f)
        curveToRelative(0.0f, 0.83f, 0.67f, 1.5f, 1.5f, 1.5f)
        reflectiveCurveToRelative(1.5f, -0.67f, 1.5f, -1.5f)
        lineTo(13.0f, 17.0f)
        horizontalLineToRelative(2.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-3.0f)
        close()
    }
}
