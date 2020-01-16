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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.TwoTone.HighlightOff: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 4.0f)
        curveToRelative(-4.41f, 0.0f, -8.0f, 3.59f, -8.0f, 8.0f)
        reflectiveCurveToRelative(3.59f, 8.0f, 8.0f, 8.0f)
        reflectiveCurveToRelative(8.0f, -3.59f, 8.0f, -8.0f)
        reflectiveCurveToRelative(-3.59f, -8.0f, -8.0f, -8.0f)
        close()
        moveTo(16.0f, 14.59f)
        lineTo(14.59f, 16.0f)
        lineTo(12.0f, 13.41f)
        lineTo(9.41f, 16.0f)
        lineTo(8.0f, 14.59f)
        lineTo(10.59f, 12.0f)
        lineTo(8.0f, 9.41f)
        lineTo(9.41f, 8.0f)
        lineTo(12.0f, 10.59f)
        lineTo(14.59f, 8.0f)
        lineTo(16.0f, 9.41f)
        lineTo(13.41f, 12.0f)
        lineTo(16.0f, 14.59f)
        close()
    }
    path {
        moveTo(14.59f, 8.0f)
        lineTo(12.0f, 10.59f)
        lineTo(9.41f, 8.0f)
        lineTo(8.0f, 9.41f)
        lineTo(10.59f, 12.0f)
        lineTo(8.0f, 14.59f)
        lineTo(9.41f, 16.0f)
        lineTo(12.0f, 13.41f)
        lineTo(14.59f, 16.0f)
        lineTo(16.0f, 14.59f)
        lineTo(13.41f, 12.0f)
        lineTo(16.0f, 9.41f)
        close()
        moveTo(12.0f, 2.0f)
        curveTo(6.47f, 2.0f, 2.0f, 6.47f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.47f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.47f, 10.0f, -10.0f)
        reflectiveCurveTo(17.53f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-4.41f, 0.0f, -8.0f, -3.59f, -8.0f, -8.0f)
        reflectiveCurveToRelative(3.59f, -8.0f, 8.0f, -8.0f)
        reflectiveCurveToRelative(8.0f, 3.59f, 8.0f, 8.0f)
        reflectiveCurveToRelative(-3.59f, 8.0f, -8.0f, 8.0f)
        close()
    }
}
