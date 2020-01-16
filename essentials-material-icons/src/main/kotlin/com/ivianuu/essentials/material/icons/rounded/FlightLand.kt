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

val Icons.Rounded.FlightLand: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.5f, 19.0f)
        horizontalLineToRelative(-17.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        reflectiveCurveToRelative(0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(17.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
        close()
        moveTo(3.51f, 11.61f)
        lineToRelative(15.83f, 4.24f)
        curveToRelative(0.8f, 0.21f, 1.62f, -0.26f, 1.84f, -1.06f)
        curveToRelative(0.21f, -0.8f, -0.26f, -1.62f, -1.06f, -1.84f)
        lineToRelative(-5.31f, -1.42f)
        lineToRelative(-2.58f, -8.45f)
        curveToRelative(-0.11f, -0.36f, -0.39f, -0.63f, -0.75f, -0.73f)
        curveToRelative(-0.68f, -0.18f, -1.35f, 0.33f, -1.35f, 1.04f)
        verticalLineToRelative(6.88f)
        lineTo(5.15f, 8.95f)
        lineTo(4.4f, 7.09f)
        curveToRelative(-0.12f, -0.29f, -0.36f, -0.51f, -0.67f, -0.59f)
        lineToRelative(-0.33f, -0.09f)
        curveToRelative(-0.32f, -0.09f, -0.63f, 0.15f, -0.63f, 0.48f)
        verticalLineToRelative(3.75f)
        curveToRelative(0.0f, 0.46f, 0.3f, 0.85f, 0.74f, 0.97f)
        close()
    }
}
