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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Sharp.ThumbsUpDown: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 5.0f)
        lineTo(5.82f, 5.0f)
        lineToRelative(0.78f, -3.78f)
        lineTo(5.38f, 0.0f)
        lineTo(0.0f, 5.38f)
        lineTo(0.0f, 14.0f)
        horizontalLineToRelative(9.24f)
        lineTo(12.0f, 7.54f)
        close()
        moveTo(14.76f, 10.0f)
        lineTo(12.0f, 16.46f)
        lineTo(12.0f, 19.0f)
        horizontalLineToRelative(6.18f)
        lineToRelative(-0.78f, 3.78f)
        lineTo(18.62f, 24.0f)
        lineTo(24.0f, 18.62f)
        lineTo(24.0f, 10.0f)
        close()
    }
}
