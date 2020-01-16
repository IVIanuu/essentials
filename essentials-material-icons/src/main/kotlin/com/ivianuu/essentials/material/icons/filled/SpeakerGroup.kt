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

package com.ivianuu.essentials.material.icons.filled

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Filled.SpeakerGroup: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.2f, 1.0f)
        lineTo(9.8f, 1.0f)
        curveTo(8.81f, 1.0f, 8.0f, 1.81f, 8.0f, 2.8f)
        verticalLineToRelative(14.4f)
        curveToRelative(0.0f, 0.99f, 0.81f, 1.79f, 1.8f, 1.79f)
        lineToRelative(8.4f, 0.01f)
        curveToRelative(0.99f, 0.0f, 1.8f, -0.81f, 1.8f, -1.8f)
        lineTo(20.0f, 2.8f)
        curveToRelative(0.0f, -0.99f, -0.81f, -1.8f, -1.8f, -1.8f)
        close()
        moveTo(14.0f, 3.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, 0.89f, 2.0f, 2.0f)
        reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
        reflectiveCurveToRelative(-2.0f, -0.89f, -2.0f, -2.0f)
        reflectiveCurveToRelative(0.9f, -2.0f, 2.0f, -2.0f)
        close()
        moveTo(14.0f, 16.5f)
        curveToRelative(-2.21f, 0.0f, -4.0f, -1.79f, -4.0f, -4.0f)
        reflectiveCurveToRelative(1.79f, -4.0f, 4.0f, -4.0f)
        reflectiveCurveToRelative(4.0f, 1.79f, 4.0f, 4.0f)
        reflectiveCurveToRelative(-1.79f, 4.0f, -4.0f, 4.0f)
        close()
    }
    path {
        moveTo(14.0f, 12.5f)
        moveToRelative(-2.5f, 0.0f)
        arcToRelative(2.5f, 2.5f, 0.0f, true, true, 5.0f, 0.0f)
        arcToRelative(2.5f, 2.5f, 0.0f, true, true, -5.0f, 0.0f)
    }
    path {
        moveTo(6.0f, 5.0f)
        horizontalLineTo(4.0f)
        verticalLineToRelative(16.0f)
        curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineTo(6.0f)
        verticalLineTo(5.0f)
        close()
    }
}
