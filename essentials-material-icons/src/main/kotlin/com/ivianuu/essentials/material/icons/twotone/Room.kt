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

val Icons.TwoTone.Room: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 4.0f)
        curveTo(9.24f, 4.0f, 7.0f, 6.24f, 7.0f, 9.0f)
        curveToRelative(0.0f, 2.85f, 2.92f, 7.21f, 5.0f, 9.88f)
        curveToRelative(2.11f, -2.69f, 5.0f, -7.0f, 5.0f, -9.88f)
        curveToRelative(0.0f, -2.76f, -2.24f, -5.0f, -5.0f, -5.0f)
        close()
        moveTo(12.0f, 11.5f)
        curveToRelative(-1.38f, 0.0f, -2.5f, -1.12f, -2.5f, -2.5f)
        reflectiveCurveToRelative(1.12f, -2.5f, 2.5f, -2.5f)
        reflectiveCurveToRelative(2.5f, 1.12f, 2.5f, 2.5f)
        reflectiveCurveToRelative(-1.12f, 2.5f, -2.5f, 2.5f)
        close()
    }
    path {
        moveTo(12.0f, 2.0f)
        curveTo(8.13f, 2.0f, 5.0f, 5.13f, 5.0f, 9.0f)
        curveToRelative(0.0f, 5.25f, 7.0f, 13.0f, 7.0f, 13.0f)
        reflectiveCurveToRelative(7.0f, -7.75f, 7.0f, -13.0f)
        curveToRelative(0.0f, -3.87f, -3.13f, -7.0f, -7.0f, -7.0f)
        close()
        moveTo(7.0f, 9.0f)
        curveToRelative(0.0f, -2.76f, 2.24f, -5.0f, 5.0f, -5.0f)
        reflectiveCurveToRelative(5.0f, 2.24f, 5.0f, 5.0f)
        curveToRelative(0.0f, 2.88f, -2.88f, 7.19f, -5.0f, 9.88f)
        curveTo(9.92f, 16.21f, 7.0f, 11.85f, 7.0f, 9.0f)
        close()
    }
    path {
        moveTo(12.0f, 9.0f)
        moveToRelative(-2.5f, 0.0f)
        arcToRelative(2.5f, 2.5f, 0.0f, true, true, 5.0f, 0.0f)
        arcToRelative(2.5f, 2.5f, 0.0f, true, true, -5.0f, 0.0f)
    }
}
