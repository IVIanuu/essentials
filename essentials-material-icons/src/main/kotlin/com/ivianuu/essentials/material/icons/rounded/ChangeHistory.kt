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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.ChangeHistory: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 7.77f)
        lineTo(18.39f, 18.0f)
        horizontalLineTo(5.61f)
        lineTo(12.0f, 7.77f)
        moveToRelative(-0.85f, -2.41f)
        lineToRelative(-8.2f, 13.11f)
        curveToRelative(-0.41f, 0.67f, 0.07f, 1.53f, 0.85f, 1.53f)
        horizontalLineToRelative(16.4f)
        curveToRelative(0.79f, 0.0f, 1.26f, -0.86f, 0.85f, -1.53f)
        lineToRelative(-8.2f, -13.11f)
        curveToRelative(-0.39f, -0.63f, -1.31f, -0.63f, -1.7f, 0.0f)
        close()
    }
}
