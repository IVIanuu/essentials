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

package com.ivianuu.essentials.material.icons.outlined

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Outlined.NetworkLocked: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(22.0f, 16.0f)
        verticalLineToRelative(-0.5f)
        curveToRelative(0.0f, -1.38f, -1.12f, -2.5f, -2.5f, -2.5f)
        reflectiveCurveTo(17.0f, 14.12f, 17.0f, 15.5f)
        verticalLineToRelative(0.5f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(4.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(5.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-4.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        close()
        moveTo(21.0f, 16.0f)
        horizontalLineToRelative(-3.0f)
        verticalLineToRelative(-0.5f)
        curveToRelative(0.0f, -0.83f, 0.67f, -1.5f, 1.5f, -1.5f)
        reflectiveCurveToRelative(1.5f, 0.67f, 1.5f, 1.5f)
        verticalLineToRelative(0.5f)
        close()
        moveTo(18.0f, 5.83f)
        verticalLineToRelative(5.43f)
        curveToRelative(0.47f, -0.16f, 0.97f, -0.26f, 1.5f, -0.26f)
        curveToRelative(0.17f, 0.0f, 0.33f, 0.03f, 0.5f, 0.05f)
        lineTo(20.0f, 1.0f)
        lineTo(1.0f, 20.0f)
        horizontalLineToRelative(13.0f)
        verticalLineToRelative(-2.0f)
        lineTo(5.83f, 18.0f)
        lineTo(18.0f, 5.83f)
        close()
    }
}
