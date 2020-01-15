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

val Icons.Rounded.DonutSmall: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(11.0f, 3.18f)
        verticalLineToRelative(17.64f)
        curveToRelative(0.0f, 0.64f, -0.59f, 1.12f, -1.21f, 0.98f)
        curveTo(5.32f, 20.8f, 2.0f, 16.79f, 2.0f, 12.0f)
        reflectiveCurveToRelative(3.32f, -8.8f, 7.79f, -9.8f)
        curveToRelative(0.62f, -0.14f, 1.21f, 0.34f, 1.21f, 0.98f)
        close()
        moveTo(13.03f, 3.18f)
        verticalLineToRelative(6.81f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(6.79f)
        curveToRelative(0.64f, 0.0f, 1.12f, -0.59f, 0.98f, -1.22f)
        curveToRelative(-0.85f, -3.76f, -3.8f, -6.72f, -7.55f, -7.57f)
        curveToRelative(-0.63f, -0.14f, -1.22f, 0.34f, -1.22f, 0.98f)
        close()
        moveTo(13.03f, 14.01f)
        verticalLineToRelative(6.81f)
        curveToRelative(0.0f, 0.64f, 0.59f, 1.12f, 1.22f, 0.98f)
        curveToRelative(3.76f, -0.85f, 6.71f, -3.82f, 7.56f, -7.58f)
        curveToRelative(0.14f, -0.62f, -0.35f, -1.22f, -0.98f, -1.22f)
        horizontalLineToRelative(-6.79f)
        curveToRelative(-0.56f, 0.01f, -1.01f, 0.46f, -1.01f, 1.01f)
        close()
    }
}
