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

val Icons.TwoTone.Timelapse: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 3.99f)
        curveToRelative(-4.42f, 0.0f, -8.0f, 3.58f, -8.0f, 8.0f)
        reflectiveCurveToRelative(3.58f, 8.0f, 8.0f, 8.0f)
        reflectiveCurveToRelative(8.0f, -3.58f, 8.0f, -8.0f)
        reflectiveCurveToRelative(-3.58f, -8.0f, -8.0f, -8.0f)
        close()
        moveTo(16.25f, 16.23f)
        curveToRelative(-2.35f, 2.34f, -6.15f, 2.34f, -8.49f, 0.0f)
        lineTo(12.0f, 11.99f)
        verticalLineToRelative(-6.0f)
        curveToRelative(1.54f, 0.0f, 3.07f, 0.59f, 4.24f, 1.76f)
        curveToRelative(2.35f, 2.34f, 2.35f, 6.14f, 0.01f, 8.48f)
        close()
    }
    path {
        moveTo(16.24f, 7.75f)
        curveToRelative(-1.17f, -1.17f, -2.7f, -1.76f, -4.24f, -1.76f)
        verticalLineToRelative(6.0f)
        lineToRelative(-4.24f, 4.24f)
        curveToRelative(2.34f, 2.34f, 6.14f, 2.34f, 8.49f, 0.0f)
        curveToRelative(2.34f, -2.34f, 2.34f, -6.14f, -0.01f, -8.48f)
        close()
        moveTo(12.0f, 1.99f)
        curveToRelative(-5.52f, 0.0f, -10.0f, 4.48f, -10.0f, 10.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveToRelative(-4.48f, -10.0f, -10.0f, -10.0f)
        close()
        moveTo(12.0f, 19.99f)
        curveToRelative(-4.42f, 0.0f, -8.0f, -3.58f, -8.0f, -8.0f)
        reflectiveCurveToRelative(3.58f, -8.0f, 8.0f, -8.0f)
        reflectiveCurveToRelative(8.0f, 3.58f, 8.0f, 8.0f)
        reflectiveCurveToRelative(-3.58f, 8.0f, -8.0f, 8.0f)
        close()
    }
}
