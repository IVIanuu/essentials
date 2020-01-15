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

val Icons.Rounded.PermScanWifi: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 3.0f)
        curveTo(7.41f, 3.0f, 3.86f, 4.53f, 0.89f, 6.59f)
        curveToRelative(-0.49f, 0.33f, -0.59f, 1.0f, -0.22f, 1.46f)
        lineToRelative(9.78f, 12.04f)
        curveToRelative(0.8f, 0.98f, 2.3f, 0.99f, 3.1f, 0.0f)
        lineToRelative(9.78f, -12.02f)
        curveToRelative(0.37f, -0.46f, 0.27f, -1.13f, -0.22f, -1.46f)
        curveTo(20.14f, 4.54f, 16.59f, 3.0f, 12.0f, 3.0f)
        close()
        moveTo(12.0f, 16.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        verticalLineToRelative(-4.0f)
        curveToRelative(0.0f, -0.55f, 0.45f, -1.0f, 1.0f, -1.0f)
        reflectiveCurveToRelative(1.0f, 0.45f, 1.0f, 1.0f)
        verticalLineToRelative(4.0f)
        curveToRelative(0.0f, 0.55f, -0.45f, 1.0f, -1.0f, 1.0f)
        close()
        moveTo(11.0f, 8.0f)
        lineTo(11.0f, 6.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
}
