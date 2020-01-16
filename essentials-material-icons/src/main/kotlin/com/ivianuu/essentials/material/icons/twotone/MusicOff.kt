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

val Icons.TwoTone.MusicOff: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(10.0f, 17.0f)
        moveToRelative(-2.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
    }
    path {
        moveTo(14.0f, 7.0f)
        horizontalLineToRelative(4.0f)
        lineTo(18.0f, 3.0f)
        horizontalLineToRelative(-6.0f)
        verticalLineToRelative(4.61f)
        lineToRelative(2.0f, 2.0f)
        close()
        moveTo(12.0f, 10.44f)
        lineTo(4.41f, 2.86f)
        lineTo(3.0f, 4.27f)
        lineToRelative(9.0f, 9.0f)
        verticalLineToRelative(0.28f)
        curveToRelative(-0.94f, -0.54f, -2.1f, -0.75f, -3.33f, -0.32f)
        curveToRelative(-1.34f, 0.48f, -2.37f, 1.67f, -2.61f, 3.07f)
        curveToRelative(-0.46f, 2.74f, 1.86f, 5.08f, 4.59f, 4.65f)
        curveToRelative(1.96f, -0.31f, 3.35f, -2.11f, 3.35f, -4.1f)
        verticalLineToRelative(-1.58f)
        lineTo(19.73f, 21.0f)
        lineToRelative(1.41f, -1.41f)
        lineTo(12.0f, 10.44f)
        close()
        moveTo(10.0f, 19.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, -0.9f, -2.0f, -2.0f)
        reflectiveCurveToRelative(0.9f, -2.0f, 2.0f, -2.0f)
        reflectiveCurveToRelative(2.0f, 0.9f, 2.0f, 2.0f)
        reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
        close()
    }
}
