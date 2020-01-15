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

val Icons.Filled.VideocamOff: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(21.0f, 6.5f)
        lineToRelative(-4.0f, 4.0f)
        verticalLineTo(7.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        horizontalLineTo(9.82f)
        lineTo(21.0f, 17.18f)
        verticalLineTo(6.5f)
        close()
        moveTo(3.27f, 2.0f)
        lineTo(2.0f, 3.27f)
        lineTo(4.73f, 6.0f)
        horizontalLineTo(4.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(10.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(0.21f, 0.0f, 0.39f, -0.08f, 0.54f, -0.18f)
        lineTo(19.73f, 21.0f)
        lineTo(21.0f, 19.73f)
        lineTo(3.27f, 2.0f)
        close()
    }
}
