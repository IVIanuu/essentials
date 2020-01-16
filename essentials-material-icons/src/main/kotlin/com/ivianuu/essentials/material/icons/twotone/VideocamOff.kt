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

val Icons.TwoTone.VideocamOff: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.39f, 8.0f)
        lineTo(15.0f, 10.61f)
        verticalLineTo(8.0f)
        close()
        moveTo(5.0f, 8.0f)
        verticalLineToRelative(8.0f)
        horizontalLineToRelative(9.73f)
        lineToRelative(-8.0f, -8.0f)
        close()
    }
    path {
        moveTo(3.41f, 1.86f)
        lineTo(2.0f, 3.27f)
        lineTo(4.73f, 6.0f)
        lineTo(4.0f, 6.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(10.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(0.21f, 0.0f, 0.39f, -0.08f, 0.55f, -0.18f)
        lineTo(19.73f, 21.0f)
        lineToRelative(1.41f, -1.41f)
        lineTo(3.41f, 1.86f)
        close()
        moveTo(5.0f, 16.0f)
        lineTo(5.0f, 8.0f)
        horizontalLineToRelative(1.73f)
        lineToRelative(8.0f, 8.0f)
        lineTo(5.0f, 16.0f)
        close()
        moveTo(15.0f, 8.0f)
        verticalLineToRelative(2.61f)
        lineToRelative(6.0f, 6.0f)
        lineTo(21.0f, 6.5f)
        lineToRelative(-4.0f, 4.0f)
        lineTo(17.0f, 7.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        horizontalLineToRelative(-5.61f)
        lineToRelative(2.0f, 2.0f)
        lineTo(15.0f, 8.0f)
        close()
    }
}
