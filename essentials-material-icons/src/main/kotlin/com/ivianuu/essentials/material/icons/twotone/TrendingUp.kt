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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.TrendingUp: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(16.0f, 6.0f)
        lineToRelative(2.29f, 2.29f)
        lineToRelative(-4.88f, 4.88f)
        lineToRelative(-4.0f, -4.0f)
        lineTo(2.0f, 16.59f)
        lineTo(3.41f, 18.0f)
        lineToRelative(6.0f, -6.0f)
        lineToRelative(4.0f, 4.0f)
        lineToRelative(6.3f, -6.29f)
        lineTo(22.0f, 12.0f)
        verticalLineTo(6.0f)
        horizontalLineToRelative(-6.0f)
        close()
    }
}
