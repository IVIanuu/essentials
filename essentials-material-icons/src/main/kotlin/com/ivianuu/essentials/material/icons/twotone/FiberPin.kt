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

val Icons.TwoTone.FiberPin: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(5.0f, 14.62f)
        horizontalLineToRelative(1.31f)
        verticalLineToRelative(-1.75f)
        horizontalLineToRelative(1.75f)
        curveToRelative(0.74f, 0.0f, 1.31f, -0.57f, 1.31f, -1.31f)
        verticalLineToRelative(-0.88f)
        curveToRelative(0.0f, -0.74f, -0.57f, -1.31f, -1.31f, -1.31f)
        lineTo(5.0f, 9.37f)
        verticalLineToRelative(5.25f)
        close()
        moveTo(6.31f, 10.69f)
        horizontalLineToRelative(1.75f)
        verticalLineToRelative(0.88f)
        lineTo(6.31f, 11.57f)
        verticalLineToRelative(-0.88f)
        close()
        moveTo(11.34f, 9.38f)
        horizontalLineToRelative(1.31f)
        verticalLineToRelative(5.25f)
        horizontalLineToRelative(-1.31f)
        close()
        moveTo(14.62f, 14.62f)
        horizontalLineToRelative(1.1f)
        verticalLineToRelative(-3.06f)
        lineToRelative(2.23f, 3.06f)
        lineTo(19.0f, 14.62f)
        lineTo(19.0f, 9.38f)
        horizontalLineToRelative(-1.09f)
        verticalLineToRelative(3.06f)
        lineToRelative(-2.19f, -3.06f)
        horizontalLineToRelative(-1.1f)
        close()
    }
    path(fillAlpha = 0.3f) {
        moveTo(4.0f, 6.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(12.0f)
        horizontalLineTo(4.0f)
        close()
    }
    path {
        moveTo(20.0f, 4.0f)
        lineTo(4.0f, 4.0f)
        curveToRelative(-1.11f, 0.0f, -1.99f, 0.89f, -1.99f, 2.0f)
        lineTo(2.0f, 18.0f)
        curveToRelative(0.0f, 1.11f, 0.89f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(1.11f, 0.0f, 2.0f, -0.89f, 2.0f, -2.0f)
        lineTo(22.0f, 6.0f)
        curveToRelative(0.0f, -1.11f, -0.89f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(20.0f, 18.0f)
        lineTo(4.0f, 18.0f)
        lineTo(4.0f, 6.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(12.0f)
        close()
    }
}
