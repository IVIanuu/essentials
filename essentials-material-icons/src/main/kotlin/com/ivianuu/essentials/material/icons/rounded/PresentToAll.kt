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

val Icons.Rounded.PresentToAll: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(21.0f, 3.0f)
        lineTo(3.0f, 3.0f)
        curveToRelative(-1.11f, 0.0f, -2.0f, 0.89f, -2.0f, 2.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.11f, 0.89f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(18.0f)
        curveToRelative(1.11f, 0.0f, 2.0f, -0.89f, 2.0f, -2.0f)
        lineTo(23.0f, 5.0f)
        curveToRelative(0.0f, -1.11f, -0.89f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(20.0f, 19.02f)
        lineTo(4.0f, 19.02f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        lineTo(3.0f, 5.98f)
        curveToRelative(0.0f, -0.55f, 0.45f, -1.0f, 1.0f, -1.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
        verticalLineToRelative(12.04f)
        curveToRelative(0.0f, 0.55f, -0.45f, 1.0f, -1.0f, 1.0f)
        close()
        moveTo(10.0f, 12.0f)
        lineTo(8.0f, 12.0f)
        lineToRelative(3.65f, -3.65f)
        curveToRelative(0.2f, -0.2f, 0.51f, -0.2f, 0.71f, 0.0f)
        lineTo(16.0f, 12.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(-4.0f)
        close()
    }
}
