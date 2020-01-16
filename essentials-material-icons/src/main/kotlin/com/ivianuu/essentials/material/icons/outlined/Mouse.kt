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

package com.ivianuu.essentials.material.icons.outlined

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Outlined.Mouse: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.0f, 9.0f)
        curveToRelative(-0.04f, -4.39f, -3.6f, -7.93f, -8.0f, -7.93f)
        reflectiveCurveTo(4.04f, 4.61f, 4.0f, 9.0f)
        verticalLineToRelative(6.0f)
        curveToRelative(0.0f, 4.42f, 3.58f, 8.0f, 8.0f, 8.0f)
        reflectiveCurveToRelative(8.0f, -3.58f, 8.0f, -8.0f)
        lineTo(20.0f, 9.0f)
        close()
        moveTo(18.0f, 9.0f)
        horizontalLineToRelative(-5.0f)
        lineTo(13.0f, 3.16f)
        curveToRelative(2.81f, 0.47f, 4.96f, 2.9f, 5.0f, 5.84f)
        close()
        moveTo(11.0f, 3.16f)
        lineTo(11.0f, 9.0f)
        lineTo(6.0f, 9.0f)
        curveToRelative(0.04f, -2.94f, 2.19f, -5.37f, 5.0f, -5.84f)
        close()
        moveTo(18.0f, 15.0f)
        curveToRelative(0.0f, 3.31f, -2.69f, 6.0f, -6.0f, 6.0f)
        reflectiveCurveToRelative(-6.0f, -2.69f, -6.0f, -6.0f)
        verticalLineToRelative(-4.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(4.0f)
        close()
    }
}
