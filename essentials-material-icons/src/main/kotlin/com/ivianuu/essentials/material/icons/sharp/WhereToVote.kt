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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Sharp.WhereToVote: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.0f)
        curveTo(8.14f, 2.0f, 5.0f, 5.14f, 5.0f, 9.0f)
        curveToRelative(0.0f, 5.25f, 7.0f, 13.0f, 7.0f, 13.0f)
        reflectiveCurveToRelative(7.0f, -7.75f, 7.0f, -13.0f)
        curveToRelative(0.0f, -3.86f, -3.14f, -7.0f, -7.0f, -7.0f)
        close()
        moveTo(10.47f, 14.0f)
        lineToRelative(-3.48f, -3.48f)
        lineTo(8.4f, 9.1f)
        lineToRelative(2.07f, 2.07f)
        lineToRelative(5.13f, -5.14f)
        lineToRelative(1.41f, 1.42f)
        lineTo(10.47f, 14.0f)
        close()
    }
}
