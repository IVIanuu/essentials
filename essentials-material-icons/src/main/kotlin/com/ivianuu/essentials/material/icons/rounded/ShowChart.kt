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

val Icons.Rounded.ShowChart: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(4.2f, 17.78f)
        lineToRelative(5.3f, -5.3f)
        lineToRelative(3.25f, 3.25f)
        curveToRelative(0.41f, 0.41f, 1.07f, 0.39f, 1.45f, -0.04f)
        lineToRelative(7.17f, -8.07f)
        curveToRelative(0.35f, -0.39f, 0.33f, -0.99f, -0.04f, -1.37f)
        curveToRelative(-0.4f, -0.4f, -1.07f, -0.39f, -1.45f, 0.04f)
        lineToRelative(-6.39f, 7.18f)
        lineToRelative(-3.29f, -3.29f)
        curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
        lineToRelative(-6.09f, 6.1f)
        curveToRelative(-0.39f, 0.39f, -0.39f, 1.02f, 0.0f, 1.41f)
        lineToRelative(0.09f, 0.09f)
        curveToRelative(0.39f, 0.39f, 1.03f, 0.39f, 1.41f, 0.0f)
        close()
    }
}
