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

val Icons.Rounded.ArrowDropDown: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(8.71f, 11.71f)
        lineToRelative(2.59f, 2.59f)
        curveToRelative(0.39f, 0.39f, 1.02f, 0.39f, 1.41f, 0.0f)
        lineToRelative(2.59f, -2.59f)
        curveToRelative(0.63f, -0.63f, 0.18f, -1.71f, -0.71f, -1.71f)
        horizontalLineTo(9.41f)
        curveToRelative(-0.89f, 0.0f, -1.33f, 1.08f, -0.7f, 1.71f)
        close()
    }
}
