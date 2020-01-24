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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.AccessAlarm: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 6.0f)
        curveToRelative(-3.87f, 0.0f, -7.0f, 3.13f, -7.0f, 7.0f)
        reflectiveCurveToRelative(3.13f, 7.0f, 7.0f, 7.0f)
        reflectiveCurveToRelative(7.0f, -3.13f, 7.0f, -7.0f)
        reflectiveCurveToRelative(-3.13f, -7.0f, -7.0f, -7.0f)
        close()
        moveTo(15.75f, 16.85f)
        lineTo(11.0f, 14.0f)
        lineTo(11.0f, 8.0f)
        horizontalLineToRelative(1.5f)
        verticalLineToRelative(5.25f)
        lineToRelative(4.0f, 2.37f)
        lineToRelative(-0.75f, 1.23f)
        close()
    }
    path {
        moveTo(12.0f, 4.0f)
        curveToRelative(-4.97f, 0.0f, -9.0f, 4.03f, -9.0f, 9.0f)
        reflectiveCurveToRelative(4.02f, 9.0f, 9.0f, 9.0f)
        curveToRelative(4.97f, 0.0f, 9.0f, -4.03f, 9.0f, -9.0f)
        reflectiveCurveToRelative(-4.03f, -9.0f, -9.0f, -9.0f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-3.87f, 0.0f, -7.0f, -3.13f, -7.0f, -7.0f)
        reflectiveCurveToRelative(3.13f, -7.0f, 7.0f, -7.0f)
        reflectiveCurveToRelative(7.0f, 3.13f, 7.0f, 7.0f)
        reflectiveCurveToRelative(-3.13f, 7.0f, -7.0f, 7.0f)
        close()
        moveTo(12.5f, 8.0f)
        lineTo(11.0f, 8.0f)
        verticalLineToRelative(6.0f)
        lineToRelative(4.75f, 2.85f)
        lineToRelative(0.75f, -1.23f)
        lineToRelative(-4.0f, -2.37f)
        close()
        moveTo(22.0f, 5.72f)
        lineToRelative(-4.6f, -3.86f)
        lineToRelative(-1.29f, 1.53f)
        lineToRelative(4.6f, 3.86f)
        close()
        moveTo(7.88f, 3.39f)
        lineTo(6.6f, 1.86f)
        lineTo(2.0f, 5.71f)
        lineToRelative(1.29f, 1.53f)
        close()
    }
}
