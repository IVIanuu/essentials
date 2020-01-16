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

package com.ivianuu.essentials.material.icons.rounded

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.BatteryStd: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(15.67f, 4.0f)
        horizontalLineTo(14.0f)
        verticalLineTo(3.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        horizontalLineToRelative(-2.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(1.0f)
        horizontalLineTo(8.33f)
        curveTo(7.6f, 4.0f, 7.0f, 4.6f, 7.0f, 5.33f)
        verticalLineToRelative(15.33f)
        curveTo(7.0f, 21.4f, 7.6f, 22.0f, 8.34f, 22.0f)
        horizontalLineToRelative(7.32f)
        curveToRelative(0.74f, 0.0f, 1.34f, -0.6f, 1.34f, -1.33f)
        verticalLineTo(5.33f)
        curveTo(17.0f, 4.6f, 16.4f, 4.0f, 15.67f, 4.0f)
        close()
    }
}
