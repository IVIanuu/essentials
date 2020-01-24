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

val Icons.Filled.DonutSmall: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(11.0f, 9.16f)
        verticalLineTo(2.0f)
        curveToRelative(-5.0f, 0.5f, -9.0f, 4.79f, -9.0f, 10.0f)
        reflectiveCurveToRelative(4.0f, 9.5f, 9.0f, 10.0f)
        verticalLineToRelative(-7.16f)
        curveToRelative(-1.0f, -0.41f, -2.0f, -1.52f, -2.0f, -2.84f)
        reflectiveCurveToRelative(1.0f, -2.43f, 2.0f, -2.84f)
        close()
        moveTo(14.86f, 11.0f)
        horizontalLineTo(22.0f)
        curveToRelative(-0.48f, -4.75f, -4.0f, -8.53f, -9.0f, -9.0f)
        verticalLineToRelative(7.16f)
        curveToRelative(1.0f, 0.3f, 1.52f, 0.98f, 1.86f, 1.84f)
        close()
        moveTo(13.0f, 14.84f)
        verticalLineTo(22.0f)
        curveToRelative(5.0f, -0.47f, 8.52f, -4.25f, 9.0f, -9.0f)
        horizontalLineToRelative(-7.14f)
        curveToRelative(-0.34f, 0.86f, -0.86f, 1.54f, -1.86f, 1.84f)
        close()
    }
}
