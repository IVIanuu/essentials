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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.Attachment: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(2.0f, 12.5f)
        curveTo(2.0f, 9.46f, 4.46f, 7.0f, 7.5f, 7.0f)
        horizontalLineTo(18.0f)
        curveToRelative(2.21f, 0.0f, 4.0f, 1.79f, 4.0f, 4.0f)
        reflectiveCurveToRelative(-1.79f, 4.0f, -4.0f, 4.0f)
        horizontalLineTo(9.5f)
        curveTo(8.12f, 15.0f, 7.0f, 13.88f, 7.0f, 12.5f)
        reflectiveCurveTo(8.12f, 10.0f, 9.5f, 10.0f)
        horizontalLineTo(17.0f)
        verticalLineToRelative(2.0f)
        horizontalLineTo(9.41f)
        curveToRelative(-0.55f, 0.0f, -0.55f, 1.0f, 0.0f, 1.0f)
        horizontalLineTo(18.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        reflectiveCurveToRelative(-0.9f, -2.0f, -2.0f, -2.0f)
        horizontalLineTo(7.5f)
        curveTo(5.57f, 9.0f, 4.0f, 10.57f, 4.0f, 12.5f)
        reflectiveCurveTo(5.57f, 16.0f, 7.5f, 16.0f)
        horizontalLineTo(17.0f)
        verticalLineToRelative(2.0f)
        horizontalLineTo(7.5f)
        curveTo(4.46f, 18.0f, 2.0f, 15.54f, 2.0f, 12.5f)
        close()
    }
}
