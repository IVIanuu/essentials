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

package com.ivianuu.essentials.material.icons.sharp

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Sharp.ScreenLockPortrait: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(9.0f, 16.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(-5.0f)
        horizontalLineToRelative(-1.0f)
        verticalLineToRelative(-0.9f)
        curveToRelative(0.0f, -1.0f, -0.69f, -1.92f, -1.68f, -2.08f)
        curveTo(11.07f, 7.83f, 10.0f, 8.79f, 10.0f, 10.0f)
        verticalLineToRelative(1.0f)
        lineTo(9.0f, 11.0f)
        verticalLineToRelative(5.0f)
        close()
        moveTo(10.8f, 10.0f)
        curveToRelative(0.0f, -0.66f, 0.54f, -1.2f, 1.2f, -1.2f)
        reflectiveCurveToRelative(1.2f, 0.54f, 1.2f, 1.2f)
        verticalLineToRelative(1.0f)
        horizontalLineToRelative(-2.4f)
        verticalLineToRelative(-1.0f)
        close()
        moveTo(19.0f, 1.0f)
        lineTo(5.0f, 1.0f)
        verticalLineToRelative(22.0f)
        horizontalLineToRelative(14.0f)
        lineTo(19.0f, 1.0f)
        close()
        moveTo(17.0f, 19.0f)
        lineTo(7.0f, 19.0f)
        lineTo(7.0f, 5.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(14.0f)
        close()
    }
}
