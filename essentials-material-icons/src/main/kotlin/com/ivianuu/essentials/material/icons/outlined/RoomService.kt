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

val Icons.Outlined.RoomService: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.98f, 17.0f)
        lineTo(2.0f, 17.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(20.0f)
        verticalLineToRelative(-2.0f)
        close()
        moveTo(21.0f, 16.0f)
        curveToRelative(-0.27f, -4.07f, -3.25f, -7.4f, -7.16f, -8.21f)
        curveToRelative(0.1f, -0.24f, 0.16f, -0.51f, 0.16f, -0.79f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        reflectiveCurveToRelative(-2.0f, 0.9f, -2.0f, 2.0f)
        curveToRelative(0.0f, 0.28f, 0.06f, 0.55f, 0.16f, 0.79f)
        curveTo(6.25f, 8.6f, 3.27f, 11.93f, 3.0f, 16.0f)
        horizontalLineToRelative(18.0f)
        close()
        moveTo(12.0f, 9.58f)
        curveToRelative(2.95f, 0.0f, 5.47f, 1.83f, 6.5f, 4.41f)
        horizontalLineToRelative(-13.0f)
        curveToRelative(1.03f, -2.58f, 3.55f, -4.41f, 6.5f, -4.41f)
        close()
    }
}
