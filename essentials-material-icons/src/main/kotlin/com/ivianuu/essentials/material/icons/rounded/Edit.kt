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

val Icons.Rounded.Edit: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(3.0f, 17.46f)
        verticalLineToRelative(3.04f)
        curveToRelative(0.0f, 0.28f, 0.22f, 0.5f, 0.5f, 0.5f)
        horizontalLineToRelative(3.04f)
        curveToRelative(0.13f, 0.0f, 0.26f, -0.05f, 0.35f, -0.15f)
        lineTo(17.81f, 9.94f)
        lineToRelative(-3.75f, -3.75f)
        lineTo(3.15f, 17.1f)
        curveToRelative(-0.1f, 0.1f, -0.15f, 0.22f, -0.15f, 0.36f)
        close()
        moveTo(20.71f, 7.04f)
        curveToRelative(0.39f, -0.39f, 0.39f, -1.02f, 0.0f, -1.41f)
        lineToRelative(-2.34f, -2.34f)
        curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
        lineToRelative(-1.83f, 1.83f)
        lineToRelative(3.75f, 3.75f)
        lineToRelative(1.83f, -1.83f)
        close()
    }
}
