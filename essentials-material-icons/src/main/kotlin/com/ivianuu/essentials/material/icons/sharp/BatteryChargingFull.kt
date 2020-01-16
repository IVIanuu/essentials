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

package com.ivianuu.essentials.material.icons.sharp

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Sharp.BatteryChargingFull: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(17.0f, 4.0f)
        horizontalLineToRelative(-3.0f)
        lineTo(14.0f, 2.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(2.0f)
        lineTo(7.0f, 4.0f)
        verticalLineToRelative(18.0f)
        horizontalLineToRelative(10.0f)
        lineTo(17.0f, 4.0f)
        close()
        moveTo(11.0f, 20.0f)
        verticalLineToRelative(-5.5f)
        lineTo(9.0f, 14.5f)
        lineTo(13.0f, 7.0f)
        verticalLineToRelative(5.5f)
        horizontalLineToRelative(2.0f)
        lineTo(11.0f, 20.0f)
        close()
    }
}
