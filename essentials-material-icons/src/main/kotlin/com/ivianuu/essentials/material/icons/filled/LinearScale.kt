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

package com.ivianuu.essentials.material.icons.filled

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Filled.LinearScale: VectorAsset by lazyMaterialIcon {
    group {
        path {
            moveTo(19.5f, 9.5f)
            curveToRelative(-1.03f, 0.0f, -1.9f, 0.62f, -2.29f, 1.5f)
            horizontalLineToRelative(-2.92f)
            curveToRelative(-0.39f, -0.88f, -1.26f, -1.5f, -2.29f, -1.5f)
            reflectiveCurveToRelative(-1.9f, 0.62f, -2.29f, 1.5f)
            horizontalLineTo(6.79f)
            curveToRelative(-0.39f, -0.88f, -1.26f, -1.5f, -2.29f, -1.5f)
            curveTo(3.12f, 9.5f, 2.0f, 10.62f, 2.0f, 12.0f)
            reflectiveCurveToRelative(1.12f, 2.5f, 2.5f, 2.5f)
            curveToRelative(1.03f, 0.0f, 1.9f, -0.62f, 2.29f, -1.5f)
            horizontalLineToRelative(2.92f)
            curveToRelative(0.39f, 0.88f, 1.26f, 1.5f, 2.29f, 1.5f)
            reflectiveCurveToRelative(1.9f, -0.62f, 2.29f, -1.5f)
            horizontalLineToRelative(2.92f)
            curveToRelative(0.39f, 0.88f, 1.26f, 1.5f, 2.29f, 1.5f)
            curveToRelative(1.38f, 0.0f, 2.5f, -1.12f, 2.5f, -2.5f)
            reflectiveCurveToRelative(-1.12f, -2.5f, -2.5f, -2.5f)
            close()
        }
    }
}
