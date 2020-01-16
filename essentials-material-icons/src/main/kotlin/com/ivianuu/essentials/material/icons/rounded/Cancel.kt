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

val Icons.Rounded.Cancel: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.0f)
        curveTo(6.47f, 2.0f, 2.0f, 6.47f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.47f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.47f, 10.0f, -10.0f)
        reflectiveCurveTo(17.53f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(16.3f, 16.3f)
        curveToRelative(-0.39f, 0.39f, -1.02f, 0.39f, -1.41f, 0.0f)
        lineTo(12.0f, 13.41f)
        lineTo(9.11f, 16.3f)
        curveToRelative(-0.39f, 0.39f, -1.02f, 0.39f, -1.41f, 0.0f)
        curveToRelative(-0.39f, -0.39f, -0.39f, -1.02f, 0.0f, -1.41f)
        lineTo(10.59f, 12.0f)
        lineTo(7.7f, 9.11f)
        curveToRelative(-0.39f, -0.39f, -0.39f, -1.02f, 0.0f, -1.41f)
        curveToRelative(0.39f, -0.39f, 1.02f, -0.39f, 1.41f, 0.0f)
        lineTo(12.0f, 10.59f)
        lineToRelative(2.89f, -2.89f)
        curveToRelative(0.39f, -0.39f, 1.02f, -0.39f, 1.41f, 0.0f)
        curveToRelative(0.39f, 0.39f, 0.39f, 1.02f, 0.0f, 1.41f)
        lineTo(13.41f, 12.0f)
        lineToRelative(2.89f, 2.89f)
        curveToRelative(0.38f, 0.38f, 0.38f, 1.02f, 0.0f, 1.41f)
        close()
    }
}
