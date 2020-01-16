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

val Icons.TwoTone.Category: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(17.5f, 17.5f)
        moveToRelative(-2.5f, 0.0f)
        arcToRelative(2.5f, 2.5f, 0.0f, true, true, 5.0f, 0.0f)
        arcToRelative(2.5f, 2.5f, 0.0f, true, true, -5.0f, 0.0f)
    }
    path(fillAlpha = 0.3f) {
        moveTo(5.0f, 15.5f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(4.0f)
        lineTo(5.0f, 19.5f)
        close()
        moveTo(12.0f, 5.84f)
        lineTo(10.07f, 9.0f)
        horizontalLineToRelative(3.86f)
        close()
    }
    path {
        moveTo(12.0f, 2.0f)
        lineToRelative(-5.5f, 9.0f)
        horizontalLineToRelative(11.0f)
        lineTo(12.0f, 2.0f)
        close()
        moveTo(12.0f, 5.84f)
        lineTo(13.93f, 9.0f)
        horizontalLineToRelative(-3.87f)
        lineTo(12.0f, 5.84f)
        close()
        moveTo(17.5f, 13.0f)
        curveToRelative(-2.49f, 0.0f, -4.5f, 2.01f, -4.5f, 4.5f)
        reflectiveCurveToRelative(2.01f, 4.5f, 4.5f, 4.5f)
        reflectiveCurveToRelative(4.5f, -2.01f, 4.5f, -4.5f)
        reflectiveCurveToRelative(-2.01f, -4.5f, -4.5f, -4.5f)
        close()
        moveTo(17.5f, 20.0f)
        curveToRelative(-1.38f, 0.0f, -2.5f, -1.12f, -2.5f, -2.5f)
        reflectiveCurveToRelative(1.12f, -2.5f, 2.5f, -2.5f)
        reflectiveCurveToRelative(2.5f, 1.12f, 2.5f, 2.5f)
        reflectiveCurveToRelative(-1.12f, 2.5f, -2.5f, 2.5f)
        close()
        moveTo(11.0f, 13.5f)
        lineTo(3.0f, 13.5f)
        verticalLineToRelative(8.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(-8.0f)
        close()
        moveTo(9.0f, 19.5f)
        lineTo(5.0f, 19.5f)
        verticalLineToRelative(-4.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(4.0f)
        close()
    }
}
