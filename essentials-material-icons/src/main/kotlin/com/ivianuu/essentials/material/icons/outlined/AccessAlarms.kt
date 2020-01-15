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

import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.ui.vector.VectorAsset

val Icons.Outlined.AccessAlarms: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(22.0f, 5.7f)
        lineToRelative(-4.6f, -3.9f)
        lineToRelative(-1.3f, 1.5f)
        lineToRelative(4.6f, 3.9f)
        lineTo(22.0f, 5.7f)
        close()
        moveTo(7.9f, 3.4f)
        lineTo(6.6f, 1.9f)
        lineTo(2.0f, 5.7f)
        lineToRelative(1.3f, 1.5f)
        lineToRelative(4.6f, -3.8f)
        close()
        moveTo(12.5f, 8.0f)
        lineTo(11.0f, 8.0f)
        verticalLineToRelative(6.0f)
        lineToRelative(4.7f, 2.9f)
        lineToRelative(0.8f, -1.2f)
        lineToRelative(-4.0f, -2.4f)
        lineTo(12.5f, 8.0f)
        close()
        moveTo(12.0f, 4.0f)
        curveToRelative(-5.0f, 0.0f, -9.0f, 4.0f, -9.0f, 9.0f)
        reflectiveCurveToRelative(4.0f, 9.0f, 9.0f, 9.0f)
        reflectiveCurveToRelative(9.0f, -4.0f, 9.0f, -9.0f)
        reflectiveCurveToRelative(-4.0f, -9.0f, -9.0f, -9.0f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-3.9f, 0.0f, -7.0f, -3.1f, -7.0f, -7.0f)
        reflectiveCurveToRelative(3.1f, -7.0f, 7.0f, -7.0f)
        reflectiveCurveToRelative(7.0f, 3.1f, 7.0f, 7.0f)
        reflectiveCurveToRelative(-3.1f, 7.0f, -7.0f, 7.0f)
        close()
    }
}
