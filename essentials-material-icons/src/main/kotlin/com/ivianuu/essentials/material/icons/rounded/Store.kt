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

val Icons.Rounded.Store: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(5.0f, 6.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
        lineTo(5.0f, 4.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        reflectiveCurveToRelative(0.45f, 1.0f, 1.0f, 1.0f)
        close()
        moveTo(20.16f, 7.8f)
        curveToRelative(-0.09f, -0.46f, -0.5f, -0.8f, -0.98f, -0.8f)
        lineTo(4.82f, 7.0f)
        curveToRelative(-0.48f, 0.0f, -0.89f, 0.34f, -0.98f, 0.8f)
        lineToRelative(-1.0f, 5.0f)
        curveToRelative(-0.12f, 0.62f, 0.35f, 1.2f, 0.98f, 1.2f)
        lineTo(4.0f, 14.0f)
        verticalLineToRelative(5.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(8.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-5.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(5.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-5.0f)
        horizontalLineToRelative(0.18f)
        curveToRelative(0.63f, 0.0f, 1.1f, -0.58f, 0.98f, -1.2f)
        lineToRelative(-1.0f, -5.0f)
        close()
        moveTo(12.0f, 18.0f)
        lineTo(6.0f, 18.0f)
        verticalLineToRelative(-4.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(4.0f)
        close()
    }
}
