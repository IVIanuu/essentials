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

val Icons.Outlined.Train: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(8.5f, 14.5f)
        moveToRelative(-1.5f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, 3.0f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, -3.0f, 0.0f)
    }
    path {
        moveTo(15.5f, 14.5f)
        moveToRelative(-1.5f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, 3.0f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, -3.0f, 0.0f)
    }
    path {
        moveTo(12.0f, 2.0f)
        curveToRelative(-4.0f, 0.0f, -8.0f, 0.5f, -8.0f, 4.0f)
        verticalLineToRelative(9.5f)
        curveTo(4.0f, 17.43f, 5.57f, 19.0f, 7.5f, 19.0f)
        lineTo(6.0f, 20.5f)
        verticalLineToRelative(0.5f)
        horizontalLineToRelative(2.0f)
        lineToRelative(2.0f, -2.0f)
        horizontalLineToRelative(4.0f)
        lineToRelative(2.0f, 2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-0.5f)
        lineTo(16.5f, 19.0f)
        curveToRelative(1.93f, 0.0f, 3.5f, -1.57f, 3.5f, -3.5f)
        lineTo(20.0f, 6.0f)
        curveToRelative(0.0f, -3.5f, -4.0f, -4.0f, -8.0f, -4.0f)
        close()
        moveTo(12.0f, 4.0f)
        curveToRelative(3.51f, 0.0f, 4.96f, 0.48f, 5.57f, 1.0f)
        lineTo(6.43f, 5.0f)
        curveToRelative(0.61f, -0.52f, 2.06f, -1.0f, 5.57f, -1.0f)
        close()
        moveTo(6.0f, 7.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(3.0f)
        lineTo(6.0f, 10.0f)
        lineTo(6.0f, 7.0f)
        close()
        moveTo(18.0f, 15.5f)
        curveToRelative(0.0f, 0.83f, -0.67f, 1.5f, -1.5f, 1.5f)
        horizontalLineToRelative(-9.0f)
        curveToRelative(-0.83f, 0.0f, -1.5f, -0.67f, -1.5f, -1.5f)
        lineTo(6.0f, 12.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(3.5f)
        close()
        moveTo(18.0f, 10.0f)
        horizontalLineToRelative(-5.0f)
        lineTo(13.0f, 7.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(3.0f)
        close()
    }
}
