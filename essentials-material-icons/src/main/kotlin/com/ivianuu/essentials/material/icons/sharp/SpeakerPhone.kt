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

val Icons.Sharp.SpeakerPhone: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(7.0f, 7.07f)
        lineTo(8.43f, 8.5f)
        curveToRelative(0.91f, -0.91f, 2.18f, -1.48f, 3.57f, -1.48f)
        reflectiveCurveToRelative(2.66f, 0.57f, 3.57f, 1.48f)
        lineTo(17.0f, 7.07f)
        curveTo(15.72f, 5.79f, 13.95f, 5.0f, 12.0f, 5.0f)
        reflectiveCurveToRelative(-3.72f, 0.79f, -5.0f, 2.07f)
        close()
        moveTo(12.0f, 1.0f)
        curveTo(8.98f, 1.0f, 6.24f, 2.23f, 4.25f, 4.21f)
        lineToRelative(1.41f, 1.41f)
        curveTo(7.28f, 4.0f, 9.53f, 3.0f, 12.0f, 3.0f)
        reflectiveCurveToRelative(4.72f, 1.0f, 6.34f, 2.62f)
        lineToRelative(1.41f, -1.41f)
        curveTo(17.76f, 2.23f, 15.02f, 1.0f, 12.0f, 1.0f)
        close()
        moveTo(15.99f, 10.01f)
        lineTo(8.0f, 10.0f)
        verticalLineToRelative(11.99f)
        horizontalLineToRelative(7.99f)
        lineTo(15.99f, 10.01f)
        close()
        moveTo(15.0f, 20.0f)
        lineTo(9.0f, 20.0f)
        verticalLineToRelative(-8.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(8.0f)
        close()
    }
}
