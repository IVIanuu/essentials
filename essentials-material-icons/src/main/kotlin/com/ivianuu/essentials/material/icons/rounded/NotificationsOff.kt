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

val Icons.Rounded.NotificationsOff: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 22.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        horizontalLineToRelative(-4.0f)
        curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
        close()
        moveTo(18.0f, 11.0f)
        curveToRelative(0.0f, -3.07f, -1.64f, -5.64f, -4.5f, -6.32f)
        lineTo(13.5f, 4.0f)
        curveToRelative(0.0f, -0.83f, -0.67f, -1.5f, -1.5f, -1.5f)
        reflectiveCurveToRelative(-1.5f, 0.67f, -1.5f, 1.5f)
        verticalLineToRelative(0.68f)
        curveToRelative(-0.24f, 0.06f, -0.47f, 0.15f, -0.69f, 0.23f)
        lineTo(18.0f, 13.1f)
        lineTo(18.0f, 11.0f)
        close()
        moveTo(5.41f, 3.35f)
        lineTo(4.0f, 4.76f)
        lineToRelative(2.81f, 2.81f)
        curveTo(6.29f, 8.57f, 6.0f, 9.73f, 6.0f, 11.0f)
        verticalLineToRelative(5.0f)
        lineToRelative(-1.29f, 1.29f)
        curveToRelative(-0.63f, 0.63f, -0.19f, 1.71f, 0.7f, 1.71f)
        horizontalLineToRelative(12.83f)
        lineToRelative(1.74f, 1.74f)
        lineToRelative(1.41f, -1.41f)
        lineTo(5.41f, 3.35f)
        close()
    }
}
