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

val Icons.Sharp.DonutSmall: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(13.0f, 9.18f)
        curveToRelative(0.85f, 0.3f, 1.51f, 0.97f, 1.82f, 1.82f)
        horizontalLineToRelative(7.13f)
        curveToRelative(-0.47f, -4.72f, -4.23f, -8.48f, -8.95f, -8.95f)
        verticalLineToRelative(7.13f)
        close()
        moveTo(11.0f, 14.82f)
        curveTo(9.84f, 14.4f, 9.0f, 13.3f, 9.0f, 12.0f)
        curveToRelative(0.0f, -1.3f, 0.84f, -2.4f, 2.0f, -2.82f)
        lineTo(11.0f, 2.05f)
        curveToRelative(-5.05f, 0.5f, -9.0f, 4.76f, -9.0f, 9.95f)
        curveToRelative(0.0f, 5.19f, 3.95f, 9.45f, 9.0f, 9.95f)
        verticalLineToRelative(-7.13f)
        close()
        moveTo(14.82f, 13.0f)
        curveToRelative(-0.3f, 0.85f, -0.97f, 1.51f, -1.82f, 1.82f)
        verticalLineToRelative(7.13f)
        curveToRelative(4.72f, -0.47f, 8.48f, -4.23f, 8.95f, -8.95f)
        horizontalLineToRelative(-7.13f)
        close()
    }
}
