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

val Icons.TwoTone.CheckCircle: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 4.0f)
        curveToRelative(-4.41f, 0.0f, -8.0f, 3.59f, -8.0f, 8.0f)
        reflectiveCurveToRelative(3.59f, 8.0f, 8.0f, 8.0f)
        reflectiveCurveToRelative(8.0f, -3.59f, 8.0f, -8.0f)
        reflectiveCurveToRelative(-3.59f, -8.0f, -8.0f, -8.0f)
        close()
        moveTo(10.0f, 17.0f)
        lineToRelative(-4.0f, -4.0f)
        lineToRelative(1.41f, -1.41f)
        lineTo(10.0f, 14.17f)
        lineToRelative(6.59f, -6.59f)
        lineTo(18.0f, 9.0f)
        lineToRelative(-8.0f, 8.0f)
        close()
    }
    path {
        moveTo(12.0f, 2.0f)
        curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-4.41f, 0.0f, -8.0f, -3.59f, -8.0f, -8.0f)
        reflectiveCurveToRelative(3.59f, -8.0f, 8.0f, -8.0f)
        reflectiveCurveToRelative(8.0f, 3.59f, 8.0f, 8.0f)
        reflectiveCurveToRelative(-3.59f, 8.0f, -8.0f, 8.0f)
        close()
        moveTo(16.59f, 7.58f)
        lineTo(10.0f, 14.17f)
        lineToRelative(-2.59f, -2.58f)
        lineTo(6.0f, 13.0f)
        lineToRelative(4.0f, 4.0f)
        lineToRelative(8.0f, -8.0f)
        close()
    }
}
