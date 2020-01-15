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
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.Subway: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(15.5f, 16.0f)
        moveToRelative(-1.0f, 0.0f)
        arcToRelative(1.0f, 1.0f, 0.0f, true, true, 2.0f, 0.0f)
        arcToRelative(1.0f, 1.0f, 0.0f, true, true, -2.0f, 0.0f)
    }
    path {
        moveTo(8.5f, 16.0f)
        moveToRelative(-1.0f, 0.0f)
        arcToRelative(1.0f, 1.0f, 0.0f, true, true, 2.0f, 0.0f)
        arcToRelative(1.0f, 1.0f, 0.0f, true, true, -2.0f, 0.0f)
    }
    path {
        moveTo(7.01f, 9.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(5.0f)
        horizontalLineToRelative(-10.0f)
        close()
        moveTo(17.8f, 2.8f)
        curveTo(16.0f, 2.09f, 13.86f, 2.0f, 12.0f, 2.0f)
        curveToRelative(-1.86f, 0.0f, -4.0f, 0.09f, -5.8f, 0.8f)
        curveTo(3.53f, 3.84f, 2.0f, 6.05f, 2.0f, 8.86f)
        lineTo(2.0f, 22.0f)
        horizontalLineToRelative(20.0f)
        lineTo(22.0f, 8.86f)
        curveToRelative(0.0f, -2.81f, -1.53f, -5.02f, -4.2f, -6.06f)
        close()
        moveTo(18.0f, 15.88f)
        curveToRelative(0.0f, 1.45f, -1.18f, 2.62f, -2.63f, 2.62f)
        lineToRelative(1.13f, 1.12f)
        lineTo(16.5f, 20.0f)
        lineTo(15.0f, 20.0f)
        lineToRelative(-1.5f, -1.5f)
        horizontalLineToRelative(-2.83f)
        lineTo(9.17f, 20.0f)
        lineTo(7.5f, 20.0f)
        verticalLineToRelative(-0.38f)
        lineToRelative(1.12f, -1.12f)
        curveTo(7.18f, 18.5f, 6.0f, 17.32f, 6.0f, 15.88f)
        lineTo(6.0f, 9.0f)
        curveToRelative(0.0f, -2.63f, 3.0f, -3.0f, 6.0f, -3.0f)
        curveToRelative(3.32f, 0.0f, 6.0f, 0.38f, 6.0f, 3.0f)
        verticalLineToRelative(6.88f)
        close()
    }
}
