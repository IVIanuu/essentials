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

package com.ivianuu.essentials.material.icons.outlined

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Outlined.Create: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(3.0f, 17.25f)
        verticalLineTo(21.0f)
        horizontalLineToRelative(3.75f)
        lineTo(17.81f, 9.94f)
        lineToRelative(-3.75f, -3.75f)
        lineTo(3.0f, 17.25f)
        close()
        moveTo(5.92f, 19.0f)
        horizontalLineTo(5.0f)
        verticalLineToRelative(-0.92f)
        lineToRelative(9.06f, -9.06f)
        lineToRelative(0.92f, 0.92f)
        lineTo(5.92f, 19.0f)
        close()
        moveTo(20.71f, 5.63f)
        lineToRelative(-2.34f, -2.34f)
        curveToRelative(-0.2f, -0.2f, -0.45f, -0.29f, -0.71f, -0.29f)
        reflectiveCurveToRelative(-0.51f, 0.1f, -0.7f, 0.29f)
        lineToRelative(-1.83f, 1.83f)
        lineToRelative(3.75f, 3.75f)
        lineToRelative(1.83f, -1.83f)
        curveToRelative(0.39f, -0.39f, 0.39f, -1.02f, 0.0f, -1.41f)
        close()
    }
}
