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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Rounded.LabelImportant: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(5.94f, 18.99f)
        horizontalLineTo(15.0f)
        curveToRelative(0.65f, 0.0f, 1.26f, -0.31f, 1.63f, -0.84f)
        lineToRelative(3.95f, -5.57f)
        curveToRelative(0.25f, -0.35f, 0.25f, -0.81f, 0.0f, -1.16f)
        lineToRelative(-3.96f, -5.58f)
        curveTo(16.26f, 5.31f, 15.65f, 5.0f, 15.0f, 5.0f)
        horizontalLineTo(5.94f)
        curveToRelative(-0.81f, 0.0f, -1.28f, 0.93f, -0.81f, 1.59f)
        lineTo(9.0f, 12.0f)
        lineToRelative(-3.87f, 5.41f)
        curveToRelative(-0.47f, 0.66f, 0.0f, 1.58f, 0.81f, 1.58f)
        close()
    }
}
