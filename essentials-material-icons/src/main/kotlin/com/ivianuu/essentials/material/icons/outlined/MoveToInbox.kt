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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Outlined.MoveToInbox: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(16.0f, 9.0f)
        horizontalLineToRelative(-2.55f)
        lineTo(13.45f, 6.0f)
        horizontalLineToRelative(-2.9f)
        verticalLineToRelative(3.0f)
        lineTo(8.0f, 9.0f)
        lineToRelative(4.0f, 4.0f)
        close()
        moveTo(19.0f, 3.0f)
        lineTo(4.99f, 3.0f)
        curveTo(3.88f, 3.0f, 3.0f, 3.9f, 3.0f, 5.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.88f, 2.0f, 1.99f, 2.0f)
        lineTo(19.0f, 21.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(21.0f, 5.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(19.0f, 19.0f)
        lineTo(5.0f, 19.0f)
        verticalLineToRelative(-3.0f)
        horizontalLineToRelative(3.56f)
        curveToRelative(0.69f, 1.19f, 1.97f, 2.0f, 3.45f, 2.0f)
        reflectiveCurveToRelative(2.75f, -0.81f, 3.45f, -2.0f)
        lineTo(19.0f, 16.0f)
        verticalLineToRelative(3.0f)
        close()
        moveTo(19.0f, 14.0f)
        horizontalLineToRelative(-4.99f)
        curveToRelative(0.0f, 1.1f, -0.9f, 2.0f, -2.0f, 2.0f)
        reflectiveCurveToRelative(-2.0f, -0.9f, -2.0f, -2.0f)
        lineTo(5.0f, 14.0f)
        lineToRelative(-0.01f, -9.0f)
        lineTo(19.0f, 5.0f)
        verticalLineToRelative(9.0f)
        close()
    }
}
