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

val Icons.Filled.GolfCourse: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(19.5f, 19.5f)
        moveToRelative(-1.5f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, 3.0f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, -3.0f, 0.0f)
    }
    path {
        moveTo(17.0f, 5.92f)
        lineTo(9.0f, 2.0f)
        verticalLineToRelative(18.0f)
        horizontalLineTo(7.0f)
        verticalLineToRelative(-1.73f)
        curveToRelative(-1.79f, 0.35f, -3.0f, 0.99f, -3.0f, 1.73f)
        curveToRelative(0.0f, 1.1f, 2.69f, 2.0f, 6.0f, 2.0f)
        reflectiveCurveToRelative(6.0f, -0.9f, 6.0f, -2.0f)
        curveToRelative(0.0f, -0.99f, -2.16f, -1.81f, -5.0f, -1.97f)
        verticalLineTo(8.98f)
        lineToRelative(6.0f, -3.06f)
        close()
    }
}
