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

package com.ivianuu.essentials.material.icons.twotone

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.FlightTakeoff: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(2.5f, 19.0f)
        horizontalLineToRelative(19.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-19.0f)
        verticalLineToRelative(-2.0f)
        close()
        moveTo(22.07f, 9.64f)
        curveToRelative(-0.21f, -0.8f, -1.04f, -1.28f, -1.84f, -1.06f)
        lineTo(14.92f, 10.0f)
        lineToRelative(-6.9f, -6.43f)
        lineToRelative(-1.93f, 0.51f)
        lineToRelative(4.14f, 7.17f)
        lineToRelative(-4.97f, 1.33f)
        lineToRelative(-1.97f, -1.54f)
        lineToRelative(-1.45f, 0.39f)
        lineToRelative(2.59f, 4.49f)
        lineTo(21.0f, 11.49f)
        curveToRelative(0.81f, -0.23f, 1.28f, -1.05f, 1.07f, -1.85f)
        close()
    }
}
