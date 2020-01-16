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

val Icons.Rounded.Build: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.09f, 2.91f)
        curveTo(10.08f, 0.9f, 7.07f, 0.49f, 4.65f, 1.67f)
        lineTo(8.28f, 5.3f)
        curveToRelative(0.39f, 0.39f, 0.39f, 1.02f, 0.0f, 1.41f)
        lineTo(6.69f, 8.3f)
        curveToRelative(-0.39f, 0.4f, -1.02f, 0.4f, -1.41f, 0.0f)
        lineTo(1.65f, 4.67f)
        curveTo(0.48f, 7.1f, 0.89f, 10.09f, 2.9f, 12.1f)
        curveToRelative(1.86f, 1.86f, 4.58f, 2.35f, 6.89f, 1.48f)
        lineToRelative(7.96f, 7.96f)
        curveToRelative(1.03f, 1.03f, 2.69f, 1.03f, 3.71f, 0.0f)
        curveToRelative(1.03f, -1.03f, 1.03f, -2.69f, 0.0f, -3.71f)
        lineTo(13.54f, 9.9f)
        curveToRelative(0.92f, -2.34f, 0.44f, -5.1f, -1.45f, -6.99f)
        close()
    }
}
