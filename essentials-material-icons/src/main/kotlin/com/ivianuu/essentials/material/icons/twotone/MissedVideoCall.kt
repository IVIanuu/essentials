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

val Icons.TwoTone.MissedVideoCall: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(15.0f, 13.5f)
        verticalLineTo(8.0f)
        horizontalLineTo(5.0f)
        verticalLineToRelative(8.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(-2.5f)
        close()
        moveTo(11.0f, 15.0f)
        lineToRelative(-3.89f, -3.89f)
        verticalLineToRelative(2.55f)
        horizontalLineTo(6.0f)
        verticalLineTo(9.22f)
        horizontalLineToRelative(4.44f)
        verticalLineToRelative(1.11f)
        horizontalLineTo(7.89f)
        lineToRelative(3.11f, 3.1f)
        lineToRelative(2.99f, -3.01f)
        lineToRelative(0.78f, 0.79f)
        lineTo(11.0f, 15.0f)
        close()
    }
    path {
        moveTo(3.0f, 17.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-3.5f)
        lineToRelative(4.0f, 4.0f)
        verticalLineToRelative(-11.0f)
        lineToRelative(-4.0f, 4.0f)
        lineTo(17.0f, 7.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        lineTo(4.0f, 6.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(10.0f)
        close()
        moveTo(5.0f, 8.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(8.0f)
        lineTo(5.0f, 16.0f)
        lineTo(5.0f, 8.0f)
        close()
        moveTo(11.0f, 13.43f)
        lineToRelative(-3.11f, -3.1f)
        horizontalLineToRelative(2.55f)
        lineTo(10.44f, 9.22f)
        lineTo(6.0f, 9.22f)
        verticalLineToRelative(4.44f)
        horizontalLineToRelative(1.11f)
        verticalLineToRelative(-2.55f)
        lineTo(11.0f, 15.0f)
        lineToRelative(3.77f, -3.79f)
        lineToRelative(-0.78f, -0.79f)
        close()
    }
}
