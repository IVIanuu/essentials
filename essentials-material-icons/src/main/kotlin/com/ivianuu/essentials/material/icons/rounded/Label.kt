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

val Icons.Rounded.Label: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(17.63f, 5.84f)
        curveTo(17.27f, 5.33f, 16.67f, 5.0f, 16.0f, 5.0f)
        lineTo(5.0f, 5.01f)
        curveTo(3.9f, 5.01f, 3.0f, 5.9f, 3.0f, 7.0f)
        verticalLineToRelative(10.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 1.99f, 2.0f, 1.99f)
        lineTo(16.0f, 19.0f)
        curveToRelative(0.67f, 0.0f, 1.27f, -0.33f, 1.63f, -0.84f)
        lineToRelative(3.96f, -5.58f)
        curveToRelative(0.25f, -0.35f, 0.25f, -0.81f, 0.0f, -1.16f)
        lineToRelative(-3.96f, -5.58f)
        close()
    }
}
