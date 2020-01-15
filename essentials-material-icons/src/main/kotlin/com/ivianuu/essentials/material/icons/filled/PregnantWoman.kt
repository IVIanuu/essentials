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
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.PregnantWoman: VectorAsset by lazyMaterialIcon {
    group {
        path {
            moveTo(9.0f, 4.0f)
            curveToRelative(0.0f, -1.11f, 0.89f, -2.0f, 2.0f, -2.0f)
            reflectiveCurveToRelative(2.0f, 0.89f, 2.0f, 2.0f)
            reflectiveCurveToRelative(-0.89f, 2.0f, -2.0f, 2.0f)
            reflectiveCurveToRelative(-2.0f, -0.89f, -2.0f, -2.0f)
            close()
            moveTo(16.0f, 13.0f)
            curveToRelative(-0.01f, -1.34f, -0.83f, -2.51f, -2.0f, -3.0f)
            curveToRelative(0.0f, -1.66f, -1.34f, -3.0f, -3.0f, -3.0f)
            reflectiveCurveToRelative(-3.0f, 1.34f, -3.0f, 3.0f)
            verticalLineToRelative(7.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(5.0f)
            horizontalLineToRelative(3.0f)
            verticalLineToRelative(-5.0f)
            horizontalLineToRelative(3.0f)
            verticalLineToRelative(-4.0f)
            close()
        }
    }
}
