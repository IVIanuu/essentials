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

val Icons.Rounded.HowToReg: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 20.0f)
        lineToRelative(-0.86f, -0.86f)
        curveToRelative(-1.18f, -1.18f, -1.17f, -3.1f, 0.02f, -4.26f)
        lineToRelative(0.84f, -0.82f)
        curveToRelative(-0.39f, -0.04f, -0.68f, -0.06f, -1.0f, -0.06f)
        curveToRelative(-2.67f, 0.0f, -8.0f, 1.34f, -8.0f, 4.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(9.0f)
        close()
        moveTo(11.0f, 12.0f)
        curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
        reflectiveCurveToRelative(-1.79f, -4.0f, -4.0f, -4.0f)
        reflectiveCurveToRelative(-4.0f, 1.79f, -4.0f, 4.0f)
        reflectiveCurveToRelative(1.79f, 4.0f, 4.0f, 4.0f)
    }
    path {
        moveTo(16.18f, 19.78f)
        curveToRelative(-0.39f, 0.39f, -1.03f, 0.39f, -1.42f, 0.0f)
        lineToRelative(-2.07f, -2.09f)
        curveToRelative(-0.38f, -0.39f, -0.38f, -1.01f, 0.0f, -1.39f)
        lineToRelative(0.01f, -0.01f)
        curveToRelative(0.39f, -0.39f, 1.02f, -0.39f, 1.4f, 0.0f)
        lineToRelative(1.37f, 1.37f)
        lineToRelative(4.43f, -4.46f)
        curveToRelative(0.39f, -0.39f, 1.02f, -0.39f, 1.41f, 0.0f)
        lineToRelative(0.01f, 0.01f)
        curveToRelative(0.38f, 0.39f, 0.38f, 1.01f, 0.0f, 1.39f)
        lineToRelative(-5.14f, 5.18f)
        close()
    }
}
