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

val Icons.Filled.FlightLand: VectorAsset by lazyMaterialIcon {
    group {
        path {
            moveTo(2.5f, 19.0f)
            horizontalLineToRelative(19.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(-19.0f)
            close()
            moveTo(9.68f, 13.27f)
            lineToRelative(4.35f, 1.16f)
            lineToRelative(5.31f, 1.42f)
            curveToRelative(0.8f, 0.21f, 1.62f, -0.26f, 1.84f, -1.06f)
            curveToRelative(0.21f, -0.8f, -0.26f, -1.62f, -1.06f, -1.84f)
            lineToRelative(-5.31f, -1.42f)
            lineToRelative(-2.76f, -9.02f)
            lineTo(10.12f, 2.0f)
            verticalLineToRelative(8.28f)
            lineTo(5.15f, 8.95f)
            lineToRelative(-0.93f, -2.32f)
            lineToRelative(-1.45f, -0.39f)
            verticalLineToRelative(5.17f)
            lineToRelative(1.6f, 0.43f)
            lineToRelative(5.31f, 1.43f)
            close()
        }
    }
}
