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

val Icons.Rounded.Navigation: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.93f, 4.26f)
        lineToRelative(6.15f, 14.99f)
        curveToRelative(0.34f, 0.83f, -0.51f, 1.66f, -1.33f, 1.29f)
        lineToRelative(-5.34f, -2.36f)
        curveToRelative(-0.26f, -0.11f, -0.55f, -0.11f, -0.81f, 0.0f)
        lineToRelative(-5.34f, 2.36f)
        curveToRelative(-0.82f, 0.36f, -1.67f, -0.46f, -1.33f, -1.29f)
        lineToRelative(6.15f, -14.99f)
        curveToRelative(0.33f, -0.83f, 1.51f, -0.83f, 1.85f, 0.0f)
        close()
    }
}
