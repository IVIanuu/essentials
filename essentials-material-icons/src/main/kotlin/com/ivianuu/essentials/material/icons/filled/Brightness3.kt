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

val Icons.Filled.Brightness3: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(9.0f, 2.0f)
        curveToRelative(-1.05f, 0.0f, -2.05f, 0.16f, -3.0f, 0.46f)
        curveToRelative(4.06f, 1.27f, 7.0f, 5.06f, 7.0f, 9.54f)
        curveToRelative(0.0f, 4.48f, -2.94f, 8.27f, -7.0f, 9.54f)
        curveToRelative(0.95f, 0.3f, 1.95f, 0.46f, 3.0f, 0.46f)
        curveToRelative(5.52f, 0.0f, 10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(14.52f, 2.0f, 9.0f, 2.0f)
        close()
    }
}
