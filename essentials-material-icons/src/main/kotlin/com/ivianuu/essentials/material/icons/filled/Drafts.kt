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

val Icons.Filled.Drafts: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(21.99f, 8.0f)
        curveToRelative(0.0f, -0.72f, -0.37f, -1.35f, -0.94f, -1.7f)
        lineTo(12.0f, 1.0f)
        lineTo(2.95f, 6.3f)
        curveTo(2.38f, 6.65f, 2.0f, 7.28f, 2.0f, 8.0f)
        verticalLineToRelative(10.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineToRelative(-0.01f, -10.0f)
        close()
        moveTo(12.0f, 13.0f)
        lineTo(3.74f, 7.84f)
        lineTo(12.0f, 3.0f)
        lineToRelative(8.26f, 4.84f)
        lineTo(12.0f, 13.0f)
        close()
    }
}
