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

val Icons.Rounded.ReportProblem: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(2.73f, 21.0f)
        horizontalLineToRelative(18.53f)
        curveToRelative(0.77f, 0.0f, 1.25f, -0.83f, 0.87f, -1.5f)
        lineToRelative(-9.27f, -16.0f)
        curveToRelative(-0.39f, -0.67f, -1.35f, -0.67f, -1.73f, 0.0f)
        lineToRelative(-9.27f, 16.0f)
        curveToRelative(-0.38f, 0.67f, 0.1f, 1.5f, 0.87f, 1.5f)
        close()
        moveTo(13.0f, 18.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(12.0f, 14.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        verticalLineToRelative(-2.0f)
        curveToRelative(0.0f, -0.55f, 0.45f, -1.0f, 1.0f, -1.0f)
        reflectiveCurveToRelative(1.0f, 0.45f, 1.0f, 1.0f)
        verticalLineToRelative(2.0f)
        curveToRelative(0.0f, 0.55f, -0.45f, 1.0f, -1.0f, 1.0f)
        close()
    }
}
