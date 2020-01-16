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

val Icons.TwoTone.Tonality: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(4.0f, 12.0f)
        curveToRelative(0.0f, 4.08f, 3.06f, 7.44f, 7.0f, 7.93f)
        verticalLineTo(4.07f)
        curveTo(7.05f, 4.56f, 4.0f, 7.92f, 4.0f, 12.0f)
        close()
    }
    path {
        moveTo(12.0f, 2.0f)
        curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(11.0f, 19.93f)
        curveToRelative(-3.94f, -0.49f, -7.0f, -3.85f, -7.0f, -7.93f)
        reflectiveCurveToRelative(3.05f, -7.44f, 7.0f, -7.93f)
        verticalLineToRelative(15.86f)
        close()
        moveTo(13.0f, 4.07f)
        curveToRelative(1.03f, 0.13f, 2.0f, 0.45f, 2.87f, 0.93f)
        lineTo(13.0f, 5.0f)
        verticalLineToRelative(-0.93f)
        close()
        moveTo(13.0f, 7.0f)
        horizontalLineToRelative(5.24f)
        curveToRelative(0.25f, 0.31f, 0.48f, 0.65f, 0.68f, 1.0f)
        lineTo(13.0f, 8.0f)
        lineTo(13.0f, 7.0f)
        close()
        moveTo(13.0f, 10.0f)
        horizontalLineToRelative(6.74f)
        curveToRelative(0.08f, 0.33f, 0.15f, 0.66f, 0.19f, 1.0f)
        lineTo(13.0f, 11.0f)
        verticalLineToRelative(-1.0f)
        close()
        moveTo(13.0f, 19.93f)
        lineTo(13.0f, 19.0f)
        horizontalLineToRelative(2.87f)
        curveToRelative(-0.87f, 0.48f, -1.84f, 0.8f, -2.87f, 0.93f)
        close()
        moveTo(18.24f, 17.0f)
        lineTo(13.0f, 17.0f)
        verticalLineToRelative(-1.0f)
        horizontalLineToRelative(5.92f)
        curveToRelative(-0.2f, 0.35f, -0.43f, 0.69f, -0.68f, 1.0f)
        close()
        moveTo(19.74f, 14.0f)
        lineTo(13.0f, 14.0f)
        verticalLineToRelative(-1.0f)
        horizontalLineToRelative(6.93f)
        curveToRelative(-0.04f, 0.34f, -0.11f, 0.67f, -0.19f, 1.0f)
        close()
    }
}
