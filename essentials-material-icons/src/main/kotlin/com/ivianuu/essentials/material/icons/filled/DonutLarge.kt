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

val Icons.Filled.DonutLarge: VectorAsset by lazyMaterialIcon {
    group {
        path {
            moveTo(11.0f, 5.08f)
            verticalLineTo(2.0f)
            curveToRelative(-5.0f, 0.5f, -9.0f, 4.81f, -9.0f, 10.0f)
            reflectiveCurveToRelative(4.0f, 9.5f, 9.0f, 10.0f)
            verticalLineToRelative(-3.08f)
            curveToRelative(-3.0f, -0.48f, -6.0f, -3.4f, -6.0f, -6.92f)
            reflectiveCurveToRelative(3.0f, -6.44f, 6.0f, -6.92f)
            close()
            moveTo(18.97f, 11.0f)
            horizontalLineTo(22.0f)
            curveToRelative(-0.47f, -5.0f, -4.0f, -8.53f, -9.0f, -9.0f)
            verticalLineToRelative(3.08f)
            curveTo(16.0f, 5.51f, 18.54f, 8.0f, 18.97f, 11.0f)
            close()
            moveTo(13.0f, 18.92f)
            verticalLineTo(22.0f)
            curveToRelative(5.0f, -0.47f, 8.53f, -4.0f, 9.0f, -9.0f)
            horizontalLineToRelative(-3.03f)
            curveToRelative(-0.43f, 3.0f, -2.97f, 5.49f, -5.97f, 5.92f)
            close()
        }
    }
}
