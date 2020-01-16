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

val Icons.Rounded.MoveToInbox: VectorAsset by lazyMaterialIcon {
    path {
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
        moveTo(19.0f, 15.0f)
        horizontalLineToRelative(-3.13f)
        curveToRelative(-0.47f, 0.0f, -0.85f, 0.34f, -0.98f, 0.8f)
        curveToRelative(-0.35f, 1.27f, -1.52f, 2.2f, -2.89f, 2.2f)
        reflectiveCurveToRelative(-2.54f, -0.93f, -2.89f, -2.2f)
        curveToRelative(-0.13f, -0.46f, -0.51f, -0.8f, -0.98f, -0.8f)
        lineTo(4.99f, 15.0f)
        lineTo(4.99f, 6.0f)
        curveToRelative(0.0f, -0.55f, 0.45f, -1.0f, 1.0f, -1.0f)
        lineTo(18.0f, 5.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
        verticalLineToRelative(9.0f)
        close()
        moveTo(16.0f, 10.0f)
        horizontalLineToRelative(-2.0f)
        lineTo(14.0f, 7.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(3.0f)
        lineTo(8.0f, 10.0f)
        lineToRelative(3.65f, 3.65f)
        curveToRelative(0.2f, 0.2f, 0.51f, 0.2f, 0.71f, 0.0f)
        lineTo(16.0f, 10.0f)
        close()
    }
}
