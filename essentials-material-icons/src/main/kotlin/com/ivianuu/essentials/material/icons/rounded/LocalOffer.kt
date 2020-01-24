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

val Icons.Rounded.LocalOffer: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(21.41f, 11.58f)
        lineToRelative(-9.0f, -9.0f)
        curveTo(12.05f, 2.22f, 11.55f, 2.0f, 11.0f, 2.0f)
        horizontalLineTo(4.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(7.0f)
        curveToRelative(0.0f, 0.55f, 0.22f, 1.05f, 0.59f, 1.42f)
        lineToRelative(9.0f, 9.0f)
        curveToRelative(0.36f, 0.36f, 0.86f, 0.58f, 1.41f, 0.58f)
        reflectiveCurveToRelative(1.05f, -0.22f, 1.41f, -0.59f)
        lineToRelative(7.0f, -7.0f)
        curveToRelative(0.37f, -0.36f, 0.59f, -0.86f, 0.59f, -1.41f)
        reflectiveCurveToRelative(-0.23f, -1.06f, -0.59f, -1.42f)
        close()
        moveTo(5.5f, 7.0f)
        curveTo(4.67f, 7.0f, 4.0f, 6.33f, 4.0f, 5.5f)
        reflectiveCurveTo(4.67f, 4.0f, 5.5f, 4.0f)
        reflectiveCurveTo(7.0f, 4.67f, 7.0f, 5.5f)
        reflectiveCurveTo(6.33f, 7.0f, 5.5f, 7.0f)
        close()
    }
}
