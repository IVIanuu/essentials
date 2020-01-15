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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Outlined.OfflineBolt: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.02f)
        curveToRelative(-5.51f, 0.0f, -9.98f, 4.47f, -9.98f, 9.98f)
        reflectiveCurveToRelative(4.47f, 9.98f, 9.98f, 9.98f)
        reflectiveCurveToRelative(9.98f, -4.47f, 9.98f, -9.98f)
        reflectiveCurveTo(17.51f, 2.02f, 12.0f, 2.02f)
        close()
        moveTo(12.0f, 19.98f)
        curveToRelative(-4.4f, 0.0f, -7.98f, -3.58f, -7.98f, -7.98f)
        reflectiveCurveTo(7.6f, 4.02f, 12.0f, 4.02f)
        reflectiveCurveTo(19.98f, 7.6f, 19.98f, 12.0f)
        reflectiveCurveTo(16.4f, 19.98f, 12.0f, 19.98f)
        close()
        moveTo(12.75f, 5.0f)
        lineToRelative(-4.5f, 8.5f)
        horizontalLineToRelative(3.14f)
        lineTo(11.39f, 19.0f)
        lineToRelative(4.36f, -8.5f)
        horizontalLineToRelative(-3.0f)
        close()
    }
}
