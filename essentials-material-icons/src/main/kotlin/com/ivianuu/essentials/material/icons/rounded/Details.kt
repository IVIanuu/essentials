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

val Icons.Rounded.Details: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(3.84f, 5.49f)
        lineToRelative(7.29f, 12.96f)
        curveToRelative(0.38f, 0.68f, 1.36f, 0.68f, 1.74f, 0.0f)
        lineToRelative(7.29f, -12.96f)
        curveToRelative(0.38f, -0.67f, -0.11f, -1.49f, -0.87f, -1.49f)
        horizontalLineTo(4.71f)
        curveToRelative(-0.76f, 0.0f, -1.25f, 0.82f, -0.87f, 1.49f)
        close()
        moveTo(6.38f, 6.0f)
        horizontalLineToRelative(11.25f)
        lineTo(12.0f, 16.0f)
        lineTo(6.38f, 6.0f)
        close()
    }
}
