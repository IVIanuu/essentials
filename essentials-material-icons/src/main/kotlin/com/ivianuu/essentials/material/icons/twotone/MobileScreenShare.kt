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

val Icons.TwoTone.MobileScreenShare: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(7.0f, 19.0f)
        horizontalLineToRelative(10.0f)
        lineTo(17.0f, 5.0f)
        lineTo(7.0f, 5.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(12.8f, 10.72f)
        verticalLineToRelative(-1.7f)
        lineTo(16.0f, 12.0f)
        lineToRelative(-3.2f, 2.99f)
        verticalLineToRelative(-1.75f)
        curveToRelative(-2.22f, 0.0f, -3.69f, 0.68f, -4.8f, 2.18f)
        curveToRelative(0.45f, -2.14f, 1.69f, -4.27f, 4.8f, -4.7f)
        close()
    }
    path {
        moveTo(17.0f, 1.0f)
        lineTo(7.0f, 1.0f)
        curveToRelative(-1.1f, 0.0f, -1.99f, 0.85f, -1.99f, 1.95f)
        verticalLineToRelative(18.0f)
        curveTo(5.01f, 22.05f, 5.9f, 23.0f, 7.0f, 23.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.95f, 2.0f, -2.05f)
        lineTo(19.0f, 3.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(17.0f, 19.0f)
        lineTo(7.0f, 19.0f)
        lineTo(7.0f, 5.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(12.8f, 13.24f)
        verticalLineToRelative(1.75f)
        lineTo(16.0f, 12.0f)
        lineToRelative(-3.2f, -2.98f)
        verticalLineToRelative(1.7f)
        curveToRelative(-3.11f, 0.43f, -4.35f, 2.56f, -4.8f, 4.7f)
        curveToRelative(1.11f, -1.5f, 2.58f, -2.18f, 4.8f, -2.18f)
        close()
    }
}
