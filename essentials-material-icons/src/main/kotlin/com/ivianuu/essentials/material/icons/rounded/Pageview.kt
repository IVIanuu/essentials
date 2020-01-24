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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.Pageview: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(11.5f, 9.0f)
        curveTo(10.12f, 9.0f, 9.0f, 10.12f, 9.0f, 11.5f)
        reflectiveCurveToRelative(1.12f, 2.5f, 2.5f, 2.5f)
        reflectiveCurveToRelative(2.5f, -1.12f, 2.5f, -2.5f)
        reflectiveCurveTo(12.88f, 9.0f, 11.5f, 9.0f)
        close()
        moveTo(20.0f, 4.0f)
        lineTo(4.0f, 4.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(22.0f, 6.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(16.08f, 17.5f)
        lineToRelative(-2.2f, -2.2f)
        curveToRelative(-0.9f, 0.58f, -2.03f, 0.84f, -3.22f, 0.62f)
        curveToRelative(-1.88f, -0.35f, -3.38f, -1.93f, -3.62f, -3.83f)
        curveToRelative(-0.38f, -3.01f, 2.18f, -5.52f, 5.21f, -5.04f)
        curveToRelative(1.88f, 0.3f, 3.39f, 1.84f, 3.7f, 3.71f)
        curveToRelative(0.19f, 1.16f, -0.08f, 2.23f, -0.64f, 3.12f)
        lineToRelative(2.2f, 2.19f)
        curveToRelative(0.39f, 0.39f, 0.39f, 1.03f, 0.0f, 1.42f)
        curveToRelative(-0.4f, 0.4f, -1.04f, 0.4f, -1.43f, 0.01f)
        close()
    }
}
