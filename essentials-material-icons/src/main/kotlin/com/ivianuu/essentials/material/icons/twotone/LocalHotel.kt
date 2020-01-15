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

package com.ivianuu.essentials.material.icons.twotone

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.LocalHotel: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(7.0f, 11.0f)
        moveToRelative(-1.0f, 0.0f)
        arcToRelative(1.0f, 1.0f, 0.0f, true, true, 2.0f, 0.0f)
        arcToRelative(1.0f, 1.0f, 0.0f, true, true, -2.0f, 0.0f)
    }
    path(fillAlpha = 0.3f) {
        moveTo(19.0f, 9.0f)
        horizontalLineToRelative(-6.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(-4.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
    }
    path {
        moveTo(4.0f, 11.0f)
        curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
        reflectiveCurveToRelative(3.0f, -1.34f, 3.0f, -3.0f)
        reflectiveCurveToRelative(-1.34f, -3.0f, -3.0f, -3.0f)
        reflectiveCurveToRelative(-3.0f, 1.34f, -3.0f, 3.0f)
        close()
        moveTo(8.0f, 11.0f)
        curveToRelative(0.0f, 0.55f, -0.45f, 1.0f, -1.0f, 1.0f)
        reflectiveCurveToRelative(-1.0f, -0.45f, -1.0f, -1.0f)
        reflectiveCurveToRelative(0.45f, -1.0f, 1.0f, -1.0f)
        reflectiveCurveToRelative(1.0f, 0.45f, 1.0f, 1.0f)
        close()
        moveTo(19.0f, 7.0f)
        horizontalLineToRelative(-8.0f)
        verticalLineToRelative(8.0f)
        lineTo(3.0f, 15.0f)
        lineTo(3.0f, 5.0f)
        lineTo(1.0f, 5.0f)
        verticalLineToRelative(15.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-3.0f)
        horizontalLineToRelative(18.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-9.0f)
        curveToRelative(0.0f, -2.21f, -1.79f, -4.0f, -4.0f, -4.0f)
        close()
        moveTo(21.0f, 15.0f)
        horizontalLineToRelative(-8.0f)
        lineTo(13.0f, 9.0f)
        horizontalLineToRelative(6.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
        verticalLineToRelative(4.0f)
        close()
    }
}
