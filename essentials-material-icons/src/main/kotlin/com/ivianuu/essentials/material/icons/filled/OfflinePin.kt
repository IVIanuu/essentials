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
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.OfflinePin: VectorAsset by lazyMaterialIcon {
    group {
        path {
            moveTo(12.0f, 2.0f)
            curveTo(6.5f, 2.0f, 2.0f, 6.5f, 2.0f, 12.0f)
            reflectiveCurveToRelative(4.5f, 10.0f, 10.0f, 10.0f)
            reflectiveCurveToRelative(10.0f, -4.5f, 10.0f, -10.0f)
            reflectiveCurveTo(17.5f, 2.0f, 12.0f, 2.0f)
            close()
            moveTo(17.0f, 18.0f)
            lineTo(7.0f, 18.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(10.0f)
            verticalLineToRelative(2.0f)
            close()
            moveTo(10.3f, 14.0f)
            lineTo(7.0f, 10.7f)
            lineToRelative(1.4f, -1.4f)
            lineToRelative(1.9f, 1.9f)
            lineToRelative(5.3f, -5.3f)
            lineTo(17.0f, 7.3f)
            lineTo(10.3f, 14.0f)
            close()
        }
    }
}
