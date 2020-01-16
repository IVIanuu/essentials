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

package com.ivianuu.essentials.material.icons.twotone

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.TwoTone.CloudQueue: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(19.0f, 12.0f)
        horizontalLineToRelative(-1.5f)
        verticalLineToRelative(-0.5f)
        curveTo(17.5f, 8.46f, 15.04f, 6.0f, 12.0f, 6.0f)
        curveToRelative(-2.52f, 0.0f, -4.63f, 1.69f, -5.29f, 4.0f)
        horizontalLineTo(6.0f)
        curveToRelative(-2.21f, 0.0f, -4.0f, 1.79f, -4.0f, 4.0f)
        reflectiveCurveToRelative(1.79f, 4.0f, 4.0f, 4.0f)
        horizontalLineToRelative(13.0f)
        curveToRelative(1.66f, 0.0f, 3.0f, -1.34f, 3.0f, -3.0f)
        reflectiveCurveToRelative(-1.34f, -3.0f, -3.0f, -3.0f)
        close()
    }
    path {
        moveTo(19.35f, 10.04f)
        curveTo(18.67f, 6.59f, 15.64f, 4.0f, 12.0f, 4.0f)
        curveTo(9.11f, 4.0f, 6.6f, 5.64f, 5.35f, 8.04f)
        curveTo(2.34f, 8.36f, 0.0f, 10.91f, 0.0f, 14.0f)
        curveToRelative(0.0f, 3.31f, 2.69f, 6.0f, 6.0f, 6.0f)
        horizontalLineToRelative(13.0f)
        curveToRelative(2.76f, 0.0f, 5.0f, -2.24f, 5.0f, -5.0f)
        curveToRelative(0.0f, -2.64f, -2.05f, -4.78f, -4.65f, -4.96f)
        close()
        moveTo(19.0f, 18.0f)
        horizontalLineTo(6.0f)
        curveToRelative(-2.21f, 0.0f, -4.0f, -1.79f, -4.0f, -4.0f)
        reflectiveCurveToRelative(1.79f, -4.0f, 4.0f, -4.0f)
        horizontalLineToRelative(0.71f)
        curveTo(7.37f, 7.69f, 9.48f, 6.0f, 12.0f, 6.0f)
        curveToRelative(3.04f, 0.0f, 5.5f, 2.46f, 5.5f, 5.5f)
        verticalLineToRelative(0.5f)
        horizontalLineTo(19.0f)
        curveToRelative(1.66f, 0.0f, 3.0f, 1.34f, 3.0f, 3.0f)
        reflectiveCurveToRelative(-1.34f, 3.0f, -3.0f, 3.0f)
        close()
    }
}
