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

val Icons.Rounded.TextFormat: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(5.0f, 18.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
        lineTo(6.0f, 17.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        close()
        moveTo(9.5f, 12.8f)
        horizontalLineToRelative(5.0f)
        lineToRelative(0.66f, 1.6f)
        curveToRelative(0.15f, 0.36f, 0.5f, 0.6f, 0.89f, 0.6f)
        curveToRelative(0.69f, 0.0f, 1.15f, -0.71f, 0.88f, -1.34f)
        lineToRelative(-3.88f, -8.97f)
        curveTo(12.87f, 4.27f, 12.46f, 4.0f, 12.0f, 4.0f)
        curveToRelative(-0.46f, 0.0f, -0.87f, 0.27f, -1.05f, 0.69f)
        lineToRelative(-3.88f, 8.97f)
        curveToRelative(-0.27f, 0.63f, 0.2f, 1.34f, 0.89f, 1.34f)
        curveToRelative(0.39f, 0.0f, 0.74f, -0.24f, 0.89f, -0.6f)
        lineToRelative(0.65f, -1.6f)
        close()
        moveTo(12.0f, 5.98f)
        lineTo(13.87f, 11.0f)
        horizontalLineToRelative(-3.74f)
        lineTo(12.0f, 5.98f)
        close()
    }
}
