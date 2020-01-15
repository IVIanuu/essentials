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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.Nature: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(13.0f, 16.12f)
        curveToRelative(3.37f, -0.4f, 6.01f, -3.19f, 6.16f, -6.64f)
        curveToRelative(0.17f, -3.87f, -3.02f, -7.25f, -6.89f, -7.31f)
        curveToRelative(-3.92f, -0.05f, -7.1f, 3.1f, -7.1f, 7.0f)
        curveToRelative(0.0f, 3.47f, 2.52f, 6.34f, 5.83f, 6.89f)
        verticalLineTo(20.0f)
        horizontalLineTo(6.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        reflectiveCurveToRelative(0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
        horizontalLineToRelative(-5.0f)
        verticalLineToRelative(-3.88f)
        close()
    }
}
