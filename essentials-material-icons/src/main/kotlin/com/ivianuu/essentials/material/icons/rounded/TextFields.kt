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

val Icons.Rounded.TextFields: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(2.5f, 5.5f)
        curveTo(2.5f, 6.33f, 3.17f, 7.0f, 4.0f, 7.0f)
        horizontalLineToRelative(3.5f)
        verticalLineToRelative(10.5f)
        curveToRelative(0.0f, 0.83f, 0.67f, 1.5f, 1.5f, 1.5f)
        reflectiveCurveToRelative(1.5f, -0.67f, 1.5f, -1.5f)
        verticalLineTo(7.0f)
        horizontalLineTo(14.0f)
        curveToRelative(0.83f, 0.0f, 1.5f, -0.67f, 1.5f, -1.5f)
        reflectiveCurveTo(14.83f, 4.0f, 14.0f, 4.0f)
        horizontalLineTo(4.0f)
        curveToRelative(-0.83f, 0.0f, -1.5f, 0.67f, -1.5f, 1.5f)
        close()
        moveTo(20.0f, 9.0f)
        horizontalLineToRelative(-6.0f)
        curveToRelative(-0.83f, 0.0f, -1.5f, 0.67f, -1.5f, 1.5f)
        reflectiveCurveTo(13.17f, 12.0f, 14.0f, 12.0f)
        horizontalLineToRelative(1.5f)
        verticalLineToRelative(5.5f)
        curveToRelative(0.0f, 0.83f, 0.67f, 1.5f, 1.5f, 1.5f)
        reflectiveCurveToRelative(1.5f, -0.67f, 1.5f, -1.5f)
        verticalLineTo(12.0f)
        horizontalLineTo(20.0f)
        curveToRelative(0.83f, 0.0f, 1.5f, -0.67f, 1.5f, -1.5f)
        reflectiveCurveTo(20.83f, 9.0f, 20.0f, 9.0f)
        close()
    }
}
