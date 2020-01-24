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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.FlashOn: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(7.0f, 3.0f)
        verticalLineToRelative(9.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(7.15f)
        curveToRelative(0.0f, 0.51f, 0.67f, 0.69f, 0.93f, 0.25f)
        lineToRelative(5.19f, -8.9f)
        curveToRelative(0.39f, -0.67f, -0.09f, -1.5f, -0.86f, -1.5f)
        horizontalLineTo(13.0f)
        lineToRelative(2.49f, -6.65f)
        curveToRelative(0.25f, -0.65f, -0.23f, -1.35f, -0.93f, -1.35f)
        horizontalLineTo(8.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        close()
    }
}
