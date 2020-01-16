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

val Icons.Rounded.WhereToVote: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.0f)
        curveTo(8.14f, 2.0f, 5.0f, 5.14f, 5.0f, 9.0f)
        curveToRelative(0.0f, 4.17f, 4.42f, 9.92f, 6.23f, 12.11f)
        curveToRelative(0.4f, 0.48f, 1.13f, 0.48f, 1.53f, 0.0f)
        curveTo(14.58f, 18.92f, 19.0f, 13.17f, 19.0f, 9.0f)
        curveToRelative(0.0f, -3.86f, -3.14f, -7.0f, -7.0f, -7.0f)
        close()
        moveTo(16.31f, 8.16f)
        lineToRelative(-5.13f, 5.13f)
        curveToRelative(-0.39f, 0.39f, -1.02f, 0.39f, -1.41f, 0.0f)
        lineTo(7.7f, 11.22f)
        curveToRelative(-0.39f, -0.39f, -0.39f, -1.03f, 0.0f, -1.42f)
        curveToRelative(0.39f, -0.39f, 1.03f, -0.39f, 1.42f, 0.0f)
        lineToRelative(1.36f, 1.36f)
        lineToRelative(4.42f, -4.42f)
        curveToRelative(0.39f, -0.39f, 1.03f, -0.39f, 1.42f, 0.0f)
        curveToRelative(0.38f, 0.4f, 0.38f, 1.03f, -0.01f, 1.42f)
        close()
    }
}
