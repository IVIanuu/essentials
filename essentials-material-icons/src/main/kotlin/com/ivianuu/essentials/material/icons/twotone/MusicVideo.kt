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

val Icons.TwoTone.MusicVideo: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(3.0f, 19.0f)
        horizontalLineToRelative(18.0f)
        lineTo(21.0f, 5.0f)
        lineTo(3.0f, 5.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(11.0f, 12.0f)
        curveToRelative(0.35f, 0.0f, 0.69f, 0.07f, 1.0f, 0.18f)
        lineTo(12.0f, 6.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-3.0f)
        verticalLineToRelative(7.03f)
        curveToRelative(-0.02f, 1.64f, -1.35f, 2.97f, -3.0f, 2.97f)
        curveToRelative(-1.66f, 0.0f, -3.0f, -1.34f, -3.0f, -3.0f)
        reflectiveCurveToRelative(1.34f, -3.0f, 3.0f, -3.0f)
        close()
    }
    path {
        moveTo(21.0f, 3.0f)
        lineTo(3.0f, 3.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(18.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(23.0f, 5.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(21.0f, 19.0f)
        lineTo(3.0f, 19.0f)
        lineTo(3.0f, 5.0f)
        horizontalLineToRelative(18.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(11.0f, 18.0f)
        curveToRelative(1.65f, 0.0f, 2.98f, -1.33f, 3.0f, -2.97f)
        lineTo(14.0f, 8.0f)
        horizontalLineToRelative(3.0f)
        lineTo(17.0f, 6.0f)
        horizontalLineToRelative(-5.0f)
        verticalLineToRelative(6.18f)
        curveToRelative(-0.31f, -0.11f, -0.65f, -0.18f, -1.0f, -0.18f)
        curveToRelative(-1.66f, 0.0f, -3.0f, 1.34f, -3.0f, 3.0f)
        reflectiveCurveToRelative(1.34f, 3.0f, 3.0f, 3.0f)
        close()
    }
}
