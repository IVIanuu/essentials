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

val Icons.TwoTone.LibraryMusic: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(8.0f, 16.0f)
        horizontalLineToRelative(12.0f)
        lineTo(20.0f, 4.0f)
        lineTo(8.0f, 4.0f)
        verticalLineToRelative(12.0f)
        close()
        moveTo(12.5f, 10.0f)
        curveToRelative(0.57f, 0.0f, 1.08f, 0.19f, 1.5f, 0.51f)
        lineTo(14.0f, 5.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-3.0f)
        verticalLineToRelative(5.5f)
        curveToRelative(0.0f, 1.38f, -1.12f, 2.5f, -2.5f, 2.5f)
        reflectiveCurveTo(10.0f, 13.88f, 10.0f, 12.5f)
        reflectiveCurveToRelative(1.12f, -2.5f, 2.5f, -2.5f)
        close()
    }
    path {
        moveTo(20.0f, 2.0f)
        lineTo(8.0f, 2.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(22.0f, 4.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(20.0f, 16.0f)
        lineTo(8.0f, 16.0f)
        lineTo(8.0f, 4.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(12.0f)
        close()
        moveTo(12.5f, 15.0f)
        curveToRelative(1.38f, 0.0f, 2.5f, -1.12f, 2.5f, -2.5f)
        lineTo(15.0f, 7.0f)
        horizontalLineToRelative(3.0f)
        lineTo(18.0f, 5.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(5.51f)
        curveToRelative(-0.42f, -0.32f, -0.93f, -0.51f, -1.5f, -0.51f)
        curveToRelative(-1.38f, 0.0f, -2.5f, 1.12f, -2.5f, 2.5f)
        reflectiveCurveToRelative(1.12f, 2.5f, 2.5f, 2.5f)
        close()
        moveTo(2.0f, 6.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(-2.0f)
        lineTo(4.0f, 20.0f)
        lineTo(4.0f, 6.0f)
        lineTo(2.0f, 6.0f)
        close()
    }
}
