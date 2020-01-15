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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.WhereToVote: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.0f)
        curveToRelative(3.86f, 0.0f, 7.0f, 3.14f, 7.0f, 7.0f)
        curveToRelative(0.0f, 5.25f, -7.0f, 13.0f, -7.0f, 13.0f)
        reflectiveCurveTo(5.0f, 14.25f, 5.0f, 9.0f)
        curveToRelative(0.0f, -3.86f, 3.14f, -7.0f, 7.0f, -7.0f)
        close()
        moveTo(10.47f, 14.0f)
        lineTo(17.0f, 7.41f)
        lineTo(15.6f, 6.0f)
        lineToRelative(-5.13f, 5.18f)
        lineTo(8.4f, 9.09f)
        lineTo(7.0f, 10.5f)
        lineToRelative(3.47f, 3.5f)
        close()
    }
}
