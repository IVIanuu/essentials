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

val Icons.TwoTone.RecentActors: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(13.0f, 7.0f)
        lineTo(3.0f, 7.0f)
        verticalLineToRelative(10.0f)
        horizontalLineToRelative(10.0f)
        lineTo(13.0f, 7.0f)
        close()
        moveTo(8.0f, 8.0f)
        curveToRelative(1.07f, 0.0f, 1.95f, 0.87f, 1.95f, 1.95f)
        curveToRelative(0.0f, 1.07f, -0.87f, 1.95f, -1.95f, 1.95f)
        reflectiveCurveToRelative(-1.95f, -0.87f, -1.95f, -1.95f)
        reflectiveCurveTo(6.93f, 8.0f, 8.0f, 8.0f)
        close()
        moveTo(11.89f, 16.0f)
        lineTo(4.11f, 16.0f)
        verticalLineToRelative(-0.65f)
        curveToRelative(0.0f, -1.3f, 2.59f, -1.95f, 3.89f, -1.95f)
        reflectiveCurveToRelative(3.89f, 0.65f, 3.89f, 1.95f)
        lineTo(11.89f, 16.0f)
        close()
    }
    path {
        moveTo(21.0f, 5.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(14.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(17.0f, 5.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(14.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(14.0f, 19.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        lineTo(15.0f, 6.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        lineTo(2.0f, 5.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(12.0f)
        close()
        moveTo(3.0f, 7.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(10.0f)
        lineTo(3.0f, 17.0f)
        lineTo(3.0f, 7.0f)
        close()
    }
    path {
        moveTo(8.0f, 9.94f)
        moveToRelative(-1.95f, 0.0f)
        arcToRelative(1.95f, 1.95f, 0.0f, true, true, 3.9f, 0.0f)
        arcToRelative(1.95f, 1.95f, 0.0f, true, true, -3.9f, 0.0f)
    }
    path {
        moveTo(8.0f, 13.4f)
        curveToRelative(-1.3f, 0.0f, -3.89f, 0.65f, -3.89f, 1.95f)
        verticalLineTo(16.0f)
        horizontalLineToRelative(7.78f)
        verticalLineToRelative(-0.65f)
        curveToRelative(0.0f, -1.3f, -2.59f, -1.95f, -3.89f, -1.95f)
        close()
    }
}
