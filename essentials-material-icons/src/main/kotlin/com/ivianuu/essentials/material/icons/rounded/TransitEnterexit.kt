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

val Icons.Rounded.TransitEnterexit: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(14.5f, 18.0f)
        horizontalLineTo(8.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, -0.9f, -2.0f, -2.0f)
        verticalLineTo(9.5f)
        curveTo(6.0f, 8.67f, 6.67f, 8.0f, 7.5f, 8.0f)
        reflectiveCurveTo(9.0f, 8.67f, 9.0f, 9.5f)
        verticalLineToRelative(3.27f)
        lineTo(14.95f, 7.0f)
        curveToRelative(0.57f, -0.55f, 1.48f, -0.54f, 2.04f, 0.02f)
        reflectiveCurveToRelative(0.56f, 1.47f, 0.01f, 2.04f)
        lineTo(11.15f, 15.0f)
        horizontalLineToRelative(3.35f)
        curveToRelative(0.83f, 0.0f, 1.5f, 0.67f, 1.5f, 1.5f)
        reflectiveCurveToRelative(-0.67f, 1.5f, -1.5f, 1.5f)
        close()
    }
}
