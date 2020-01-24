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

package com.ivianuu.essentials.material.icons.twotone

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.Sync: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.01f, 4.0f)
        lineTo(12.01f, 1.0f)
        lineToRelative(-4.0f, 4.0f)
        lineToRelative(4.0f, 4.0f)
        lineTo(12.01f, 6.0f)
        curveToRelative(3.31f, 0.0f, 6.0f, 2.69f, 6.0f, 6.0f)
        curveToRelative(0.0f, 1.01f, -0.25f, 1.97f, -0.7f, 2.8f)
        lineToRelative(1.46f, 1.46f)
        curveToRelative(0.78f, -1.23f, 1.24f, -2.69f, 1.24f, -4.26f)
        curveToRelative(0.0f, -4.42f, -3.58f, -8.0f, -8.0f, -8.0f)
        close()
        moveTo(12.01f, 18.0f)
        curveToRelative(-3.31f, 0.0f, -6.0f, -2.69f, -6.0f, -6.0f)
        curveToRelative(0.0f, -1.01f, 0.25f, -1.97f, 0.7f, -2.8f)
        lineTo(5.25f, 7.74f)
        curveTo(4.47f, 8.97f, 4.01f, 10.43f, 4.01f, 12.0f)
        curveToRelative(0.0f, 4.42f, 3.58f, 8.0f, 8.0f, 8.0f)
        verticalLineToRelative(3.0f)
        lineToRelative(4.0f, -4.0f)
        lineToRelative(-4.0f, -4.0f)
        verticalLineToRelative(3.0f)
        close()
    }
}
