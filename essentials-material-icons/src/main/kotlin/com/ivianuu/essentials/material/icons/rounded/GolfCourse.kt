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

val Icons.Rounded.GolfCourse: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(19.5f, 19.5f)
        moveToRelative(-1.5f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, 3.0f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, -3.0f, 0.0f)
    }
    path {
        moveTo(11.0f, 18.03f)
        verticalLineTo(8.98f)
        lineToRelative(4.22f, -2.15f)
        curveToRelative(0.73f, -0.37f, 0.73f, -1.43f, -0.01f, -1.79f)
        lineToRelative(-4.76f, -2.33f)
        curveTo(9.78f, 2.38f, 9.0f, 2.86f, 9.0f, 3.6f)
        verticalLineTo(19.0f)
        curveToRelative(0.0f, 0.55f, -0.45f, 1.0f, -1.0f, 1.0f)
        reflectiveCurveToRelative(-1.0f, -0.45f, -1.0f, -1.0f)
        verticalLineToRelative(-0.73f)
        curveToRelative(-1.79f, 0.35f, -3.0f, 0.99f, -3.0f, 1.73f)
        curveToRelative(0.0f, 1.1f, 2.69f, 2.0f, 6.0f, 2.0f)
        reflectiveCurveToRelative(6.0f, -0.9f, 6.0f, -2.0f)
        curveToRelative(0.0f, -0.99f, -2.16f, -1.81f, -5.0f, -1.97f)
        close()
    }
}
