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

val Icons.Rounded.FiberSmartRecord: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(9.0f, 12.0f)
        moveToRelative(-8.0f, 0.0f)
        arcToRelative(8.0f, 8.0f, 0.0f, true, true, 16.0f, 0.0f)
        arcToRelative(8.0f, 8.0f, 0.0f, true, true, -16.0f, 0.0f)
    }
    path {
        moveTo(17.0f, 5.55f)
        verticalLineToRelative(0.18f)
        curveToRelative(0.0f, 0.37f, 0.23f, 0.69f, 0.57f, 0.85f)
        curveTo(19.6f, 7.54f, 21.0f, 9.61f, 21.0f, 12.0f)
        reflectiveCurveToRelative(-1.4f, 4.46f, -3.43f, 5.42f)
        curveToRelative(-0.34f, 0.16f, -0.57f, 0.47f, -0.57f, 0.84f)
        verticalLineToRelative(0.18f)
        curveToRelative(0.0f, 0.68f, 0.71f, 1.11f, 1.32f, 0.82f)
        curveTo(21.08f, 18.01f, 23.0f, 15.23f, 23.0f, 12.0f)
        reflectiveCurveToRelative(-1.92f, -6.01f, -4.68f, -7.27f)
        curveToRelative(-0.61f, -0.28f, -1.32f, 0.14f, -1.32f, 0.82f)
        close()
    }
}
