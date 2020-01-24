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

import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path
import androidx.ui.graphics.vector.VectorAsset

val Icons.Sharp.AccountBalanceWallet: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(21.0f, 18.0f)
        verticalLineToRelative(3.0f)
        lineTo(3.0f, 21.0f)
        lineTo(3.0f, 3.0f)
        horizontalLineToRelative(18.0f)
        verticalLineToRelative(3.0f)
        lineTo(10.0f, 6.0f)
        verticalLineToRelative(12.0f)
        horizontalLineToRelative(11.0f)
        close()
        moveTo(12.0f, 16.0f)
        horizontalLineToRelative(10.0f)
        lineTo(22.0f, 8.0f)
        lineTo(12.0f, 8.0f)
        verticalLineToRelative(8.0f)
        close()
        moveTo(16.0f, 13.5f)
        curveToRelative(-0.83f, 0.0f, -1.5f, -0.67f, -1.5f, -1.5f)
        reflectiveCurveToRelative(0.67f, -1.5f, 1.5f, -1.5f)
        reflectiveCurveToRelative(1.5f, 0.67f, 1.5f, 1.5f)
        reflectiveCurveToRelative(-0.67f, 1.5f, -1.5f, 1.5f)
        close()
    }
}
