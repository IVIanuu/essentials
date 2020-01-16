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

val Icons.Sharp.RingVolume: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(21.16f, 6.26f)
        lineToRelative(-1.41f, -1.41f)
        lineToRelative(-3.56f, 3.55f)
        lineToRelative(1.41f, 1.41f)
        reflectiveCurveToRelative(3.45f, -3.52f, 3.56f, -3.55f)
        close()
        moveTo(11.0f, 2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(5.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(6.4f, 9.81f)
        lineTo(7.81f, 8.4f)
        lineTo(4.26f, 4.84f)
        lineTo(2.84f, 6.26f)
        curveToRelative(0.11f, 0.03f, 3.56f, 3.55f, 3.56f, 3.55f)
        close()
        moveTo(0.0f, 17.39f)
        lineToRelative(3.68f, 3.68f)
        lineToRelative(3.92f, -3.11f)
        verticalLineToRelative(-3.37f)
        curveToRelative(2.85f, -0.93f, 5.94f, -0.93f, 8.8f, 0.0f)
        verticalLineToRelative(3.38f)
        lineToRelative(3.91f, 3.1f)
        lineTo(24.0f, 17.39f)
        curveToRelative(-6.41f, -7.19f, -17.59f, -7.19f, -24.0f, 0.0f)
        close()
    }
}
