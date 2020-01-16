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

package com.ivianuu.essentials.material.icons.rounded

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Rounded.RecentActors: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(21.0f, 6.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
        lineTo(23.0f, 6.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        reflectiveCurveToRelative(-1.0f, 0.45f, -1.0f, 1.0f)
        close()
        moveTo(18.0f, 19.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        lineTo(19.0f, 6.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        reflectiveCurveToRelative(-1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        close()
        moveTo(14.0f, 5.0f)
        lineTo(2.0f, 5.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        lineTo(15.0f, 6.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        close()
        moveTo(8.0f, 7.75f)
        curveToRelative(1.24f, 0.0f, 2.25f, 1.01f, 2.25f, 2.25f)
        reflectiveCurveTo(9.24f, 12.25f, 8.0f, 12.25f)
        reflectiveCurveTo(5.75f, 11.24f, 5.75f, 10.0f)
        reflectiveCurveTo(6.76f, 7.75f, 8.0f, 7.75f)
        close()
        moveTo(12.5f, 17.0f)
        horizontalLineToRelative(-9.0f)
        verticalLineToRelative(-0.75f)
        curveToRelative(0.0f, -1.5f, 3.0f, -2.25f, 4.5f, -2.25f)
        reflectiveCurveToRelative(4.5f, 0.75f, 4.5f, 2.25f)
        lineTo(12.5f, 17.0f)
        close()
    }
}
