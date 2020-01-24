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

val Icons.Filled.Looks: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 10.0f)
        curveToRelative(-3.86f, 0.0f, -7.0f, 3.14f, -7.0f, 7.0f)
        horizontalLineToRelative(2.0f)
        curveToRelative(0.0f, -2.76f, 2.24f, -5.0f, 5.0f, -5.0f)
        reflectiveCurveToRelative(5.0f, 2.24f, 5.0f, 5.0f)
        horizontalLineToRelative(2.0f)
        curveToRelative(0.0f, -3.86f, -3.14f, -7.0f, -7.0f, -7.0f)
        close()
        moveTo(12.0f, 6.0f)
        curveTo(5.93f, 6.0f, 1.0f, 10.93f, 1.0f, 17.0f)
        horizontalLineToRelative(2.0f)
        curveToRelative(0.0f, -4.96f, 4.04f, -9.0f, 9.0f, -9.0f)
        reflectiveCurveToRelative(9.0f, 4.04f, 9.0f, 9.0f)
        horizontalLineToRelative(2.0f)
        curveToRelative(0.0f, -6.07f, -4.93f, -11.0f, -11.0f, -11.0f)
        close()
    }
}
