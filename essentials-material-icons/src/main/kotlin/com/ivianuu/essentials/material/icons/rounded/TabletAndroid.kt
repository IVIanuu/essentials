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

val Icons.Rounded.TabletAndroid: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.0f, 0.0f)
        lineTo(6.0f, 0.0f)
        curveTo(4.34f, 0.0f, 3.0f, 1.34f, 3.0f, 3.0f)
        verticalLineToRelative(18.0f)
        curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(1.66f, 0.0f, 3.0f, -1.34f, 3.0f, -3.0f)
        lineTo(21.0f, 3.0f)
        curveToRelative(0.0f, -1.66f, -1.34f, -3.0f, -3.0f, -3.0f)
        close()
        moveTo(13.5f, 22.0f)
        horizontalLineToRelative(-3.0f)
        curveToRelative(-0.28f, 0.0f, -0.5f, -0.22f, -0.5f, -0.5f)
        reflectiveCurveToRelative(0.22f, -0.5f, 0.5f, -0.5f)
        horizontalLineToRelative(3.0f)
        curveToRelative(0.28f, 0.0f, 0.5f, 0.22f, 0.5f, 0.5f)
        reflectiveCurveToRelative(-0.22f, 0.5f, -0.5f, 0.5f)
        close()
        moveTo(19.25f, 19.0f)
        lineTo(4.75f, 19.0f)
        lineTo(4.75f, 3.0f)
        horizontalLineToRelative(14.5f)
        verticalLineToRelative(16.0f)
        close()
    }
}
