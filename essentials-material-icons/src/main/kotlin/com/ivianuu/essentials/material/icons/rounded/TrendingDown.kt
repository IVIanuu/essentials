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

val Icons.Rounded.TrendingDown: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(16.85f, 17.15f)
        lineToRelative(1.44f, -1.44f)
        lineToRelative(-4.88f, -4.88f)
        lineToRelative(-3.29f, 3.29f)
        curveToRelative(-0.39f, 0.39f, -1.02f, 0.39f, -1.41f, 0.0f)
        lineToRelative(-6.0f, -6.01f)
        curveToRelative(-0.39f, -0.39f, -0.39f, -1.02f, 0.0f, -1.41f)
        curveToRelative(0.39f, -0.39f, 1.02f, -0.39f, 1.41f, 0.0f)
        lineTo(9.41f, 12.0f)
        lineToRelative(3.29f, -3.29f)
        curveToRelative(0.39f, -0.39f, 1.02f, -0.39f, 1.41f, 0.0f)
        lineToRelative(5.59f, 5.58f)
        lineToRelative(1.44f, -1.44f)
        curveToRelative(0.31f, -0.31f, 0.85f, -0.09f, 0.85f, 0.35f)
        verticalLineToRelative(4.29f)
        curveToRelative(0.0f, 0.28f, -0.22f, 0.5f, -0.5f, 0.5f)
        horizontalLineTo(17.2f)
        curveToRelative(-0.44f, 0.01f, -0.66f, -0.53f, -0.35f, -0.84f)
        close()
    }
}
