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

val Icons.Rounded.Opacity: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(17.66f, 8.0f)
        lineToRelative(-4.95f, -4.94f)
        curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
        lineTo(6.34f, 8.0f)
        curveTo(4.78f, 9.56f, 4.0f, 11.64f, 4.0f, 13.64f)
        reflectiveCurveToRelative(0.78f, 4.11f, 2.34f, 5.67f)
        reflectiveCurveToRelative(3.61f, 2.35f, 5.66f, 2.35f)
        reflectiveCurveToRelative(4.1f, -0.79f, 5.66f, -2.35f)
        reflectiveCurveTo(20.0f, 15.64f, 20.0f, 13.64f)
        reflectiveCurveTo(19.22f, 9.56f, 17.66f, 8.0f)
        close()
        moveTo(6.0f, 14.0f)
        curveToRelative(0.01f, -2.0f, 0.62f, -3.27f, 1.76f, -4.4f)
        lineTo(12.0f, 5.27f)
        lineToRelative(4.24f, 4.38f)
        curveTo(17.38f, 10.77f, 17.99f, 12.0f, 18.0f, 14.0f)
        horizontalLineTo(6.0f)
        close()
    }
}
