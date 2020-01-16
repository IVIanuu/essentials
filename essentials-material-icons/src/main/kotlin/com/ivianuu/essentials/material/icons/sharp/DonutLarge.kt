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

val Icons.Sharp.DonutLarge: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(13.0f, 5.08f)
        curveToRelative(3.06f, 0.44f, 5.48f, 2.86f, 5.92f, 5.92f)
        horizontalLineToRelative(3.03f)
        curveToRelative(-0.47f, -4.72f, -4.23f, -8.48f, -8.95f, -8.95f)
        verticalLineToRelative(3.03f)
        close()
        moveTo(18.92f, 13.0f)
        curveToRelative(-0.44f, 3.06f, -2.86f, 5.48f, -5.92f, 5.92f)
        verticalLineToRelative(3.03f)
        curveToRelative(4.72f, -0.47f, 8.48f, -4.23f, 8.95f, -8.95f)
        horizontalLineToRelative(-3.03f)
        close()
        moveTo(11.0f, 18.92f)
        curveToRelative(-3.39f, -0.49f, -6.0f, -3.4f, -6.0f, -6.92f)
        reflectiveCurveToRelative(2.61f, -6.43f, 6.0f, -6.92f)
        verticalLineTo(2.05f)
        curveToRelative(-5.05f, 0.5f, -9.0f, 4.76f, -9.0f, 9.95f)
        curveToRelative(0.0f, 5.19f, 3.95f, 9.45f, 9.0f, 9.95f)
        verticalLineToRelative(-3.03f)
        close()
    }
}
