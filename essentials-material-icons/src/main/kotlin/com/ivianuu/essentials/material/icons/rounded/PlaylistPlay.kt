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

val Icons.Rounded.PlaylistPlay: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(5.0f, 10.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
        reflectiveCurveToRelative(-0.45f, 1.0f, -1.0f, 1.0f)
        lineTo(5.0f, 12.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        reflectiveCurveToRelative(0.45f, -1.0f, 1.0f, -1.0f)
        close()
        moveTo(5.0f, 6.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
        reflectiveCurveToRelative(-0.45f, 1.0f, -1.0f, 1.0f)
        lineTo(5.0f, 8.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        reflectiveCurveToRelative(0.45f, -1.0f, 1.0f, -1.0f)
        close()
        moveTo(5.0f, 14.0f)
        horizontalLineToRelative(6.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
        reflectiveCurveToRelative(-0.45f, 1.0f, -1.0f, 1.0f)
        lineTo(5.0f, 16.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        reflectiveCurveToRelative(0.45f, -1.0f, 1.0f, -1.0f)
        close()
        moveTo(14.0f, 14.88f)
        verticalLineToRelative(4.23f)
        curveToRelative(0.0f, 0.39f, 0.42f, 0.63f, 0.76f, 0.43f)
        lineToRelative(3.53f, -2.12f)
        curveToRelative(0.32f, -0.19f, 0.32f, -0.66f, 0.0f, -0.86f)
        lineToRelative(-3.53f, -2.12f)
        curveToRelative(-0.34f, -0.19f, -0.76f, 0.05f, -0.76f, 0.44f)
        close()
    }
}
