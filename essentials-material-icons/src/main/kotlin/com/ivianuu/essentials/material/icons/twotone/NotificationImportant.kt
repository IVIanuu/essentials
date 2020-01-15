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

val Icons.TwoTone.NotificationImportant: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 6.0f)
        curveToRelative(-2.76f, 0.0f, -5.0f, 2.24f, -5.0f, 5.0f)
        verticalLineToRelative(7.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(-7.0f)
        curveToRelative(0.0f, -2.76f, -2.24f, -5.0f, -5.0f, -5.0f)
        close()
        moveTo(13.0f, 16.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(13.0f, 12.0f)
        horizontalLineToRelative(-2.0f)
        lineTo(11.0f, 8.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(4.0f)
        close()
    }
    path {
        moveTo(12.0f, 23.0f)
        curveToRelative(1.1f, 0.0f, 1.99f, -0.89f, 1.99f, -1.99f)
        horizontalLineToRelative(-3.98f)
        curveToRelative(0.0f, 1.1f, 0.89f, 1.99f, 1.99f, 1.99f)
        close()
        moveTo(19.0f, 17.0f)
        verticalLineToRelative(-6.0f)
        curveToRelative(0.0f, -3.35f, -2.36f, -6.15f, -5.5f, -6.83f)
        lineTo(13.5f, 3.0f)
        curveToRelative(0.0f, -0.83f, -0.67f, -1.5f, -1.5f, -1.5f)
        reflectiveCurveToRelative(-1.5f, 0.67f, -1.5f, 1.5f)
        verticalLineToRelative(1.17f)
        curveTo(7.36f, 4.85f, 5.0f, 7.65f, 5.0f, 11.0f)
        verticalLineToRelative(6.0f)
        lineToRelative(-2.0f, 2.0f)
        verticalLineToRelative(1.0f)
        horizontalLineToRelative(18.0f)
        verticalLineToRelative(-1.0f)
        lineToRelative(-2.0f, -2.0f)
        close()
        moveTo(17.0f, 18.0f)
        lineTo(7.0f, 18.0f)
        verticalLineToRelative(-7.0f)
        curveToRelative(0.0f, -2.76f, 2.24f, -5.0f, 5.0f, -5.0f)
        reflectiveCurveToRelative(5.0f, 2.24f, 5.0f, 5.0f)
        verticalLineToRelative(7.0f)
        close()
        moveTo(11.0f, 8.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(11.0f, 14.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
}
