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

val Icons.Rounded.LastPage: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(6.29f, 8.11f)
        lineTo(10.18f, 12.0f)
        lineToRelative(-3.89f, 3.89f)
        curveToRelative(-0.39f, 0.39f, -0.39f, 1.02f, 0.0f, 1.41f)
        curveToRelative(0.39f, 0.39f, 1.02f, 0.39f, 1.41f, 0.0f)
        lineToRelative(4.59f, -4.59f)
        curveToRelative(0.39f, -0.39f, 0.39f, -1.02f, 0.0f, -1.41f)
        lineTo(7.7f, 6.7f)
        curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
        curveToRelative(-0.38f, 0.39f, -0.38f, 1.03f, 0.0f, 1.41f)
        close()
        moveTo(17.0f, 6.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
        verticalLineToRelative(10.0f)
        curveToRelative(0.0f, 0.55f, -0.45f, 1.0f, -1.0f, 1.0f)
        reflectiveCurveToRelative(-1.0f, -0.45f, -1.0f, -1.0f)
        verticalLineTo(7.0f)
        curveToRelative(0.0f, -0.55f, 0.45f, -1.0f, 1.0f, -1.0f)
        close()
    }
}
