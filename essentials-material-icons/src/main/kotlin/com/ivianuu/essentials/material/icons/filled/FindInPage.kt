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

val Icons.Filled.FindInPage: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.0f, 19.59f)
        verticalLineTo(8.0f)
        lineToRelative(-6.0f, -6.0f)
        horizontalLineTo(6.0f)
        curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
        lineTo(4.0f, 20.0f)
        curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 1.99f, 2.0f)
        horizontalLineTo(18.0f)
        curveToRelative(0.45f, 0.0f, 0.85f, -0.15f, 1.19f, -0.4f)
        lineToRelative(-4.43f, -4.43f)
        curveToRelative(-0.8f, 0.52f, -1.74f, 0.83f, -2.76f, 0.83f)
        curveToRelative(-2.76f, 0.0f, -5.0f, -2.24f, -5.0f, -5.0f)
        reflectiveCurveToRelative(2.24f, -5.0f, 5.0f, -5.0f)
        reflectiveCurveToRelative(5.0f, 2.24f, 5.0f, 5.0f)
        curveToRelative(0.0f, 1.02f, -0.31f, 1.96f, -0.83f, 2.75f)
        lineTo(20.0f, 19.59f)
        close()
        moveTo(9.0f, 13.0f)
        curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
        reflectiveCurveToRelative(3.0f, -1.34f, 3.0f, -3.0f)
        reflectiveCurveToRelative(-1.34f, -3.0f, -3.0f, -3.0f)
        reflectiveCurveToRelative(-3.0f, 1.34f, -3.0f, 3.0f)
        close()
    }
}
