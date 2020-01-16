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

val Icons.Filled.LabelOff: VectorAsset by lazyMaterialIcon(width = 20.0f, height = 20.0f) {
    path {
        moveTo(16.45f, 13.44f)
        lineTo(7.0f, 4.0f)
        horizontalLineToRelative(6.63f)
        curveToRelative(0.6f, 0.0f, 1.14f, 0.29f, 1.46f, 0.72f)
        lineTo(19.0f, 10.0f)
        lineToRelative(-2.55f, 3.44f)
        close()
        moveTo(1.0f, 3.0f)
        lineToRelative(1.59f, 1.59f)
        curveTo(2.22f, 4.95f, 2.0f, 5.45f, 2.0f, 6.0f)
        verticalLineToRelative(8.0f)
        curveToRelative(0.0f, 1.11f, 0.89f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(10.0f)
        lineToRelative(2.0f, 2.0f)
        lineToRelative(1.26f, -1.26f)
        lineTo(2.25f, 1.75f)
        lineTo(1.0f, 3.0f)
        close()
    }
}
