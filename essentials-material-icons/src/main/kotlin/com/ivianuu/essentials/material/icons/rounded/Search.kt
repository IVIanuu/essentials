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

val Icons.Rounded.Search: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(15.5f, 14.0f)
        horizontalLineToRelative(-0.79f)
        lineToRelative(-0.28f, -0.27f)
        curveToRelative(1.2f, -1.4f, 1.82f, -3.31f, 1.48f, -5.34f)
        curveToRelative(-0.47f, -2.78f, -2.79f, -5.0f, -5.59f, -5.34f)
        curveToRelative(-4.23f, -0.52f, -7.79f, 3.04f, -7.27f, 7.27f)
        curveToRelative(0.34f, 2.8f, 2.56f, 5.12f, 5.34f, 5.59f)
        curveToRelative(2.03f, 0.34f, 3.94f, -0.28f, 5.34f, -1.48f)
        lineToRelative(0.27f, 0.28f)
        verticalLineToRelative(0.79f)
        lineToRelative(4.25f, 4.25f)
        curveToRelative(0.41f, 0.41f, 1.08f, 0.41f, 1.49f, 0.0f)
        curveToRelative(0.41f, -0.41f, 0.41f, -1.08f, 0.0f, -1.49f)
        lineTo(15.5f, 14.0f)
        close()
        moveTo(9.5f, 14.0f)
        curveTo(7.01f, 14.0f, 5.0f, 11.99f, 5.0f, 9.5f)
        reflectiveCurveTo(7.01f, 5.0f, 9.5f, 5.0f)
        reflectiveCurveTo(14.0f, 7.01f, 14.0f, 9.5f)
        reflectiveCurveTo(11.99f, 14.0f, 9.5f, 14.0f)
        close()
    }
}
