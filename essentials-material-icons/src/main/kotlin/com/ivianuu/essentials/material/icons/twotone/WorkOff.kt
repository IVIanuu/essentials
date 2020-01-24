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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.WorkOff: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(4.0f, 8.0f)
        verticalLineToRelative(11.0f)
        horizontalLineToRelative(13.74f)
        lineToRelative(-11.0f, -11.0f)
        close()
        moveTo(12.4f, 8.0f)
        lineToRelative(7.6f, 7.6f)
        lineTo(20.0f, 8.0f)
        close()
    }
    path {
        moveTo(10.0f, 4.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-3.6f)
        lineToRelative(2.0f, 2.0f)
        horizontalLineTo(20.0f)
        verticalLineToRelative(7.6f)
        lineToRelative(2.0f, 2.0f)
        verticalLineTo(8.0f)
        curveToRelative(0.0f, -1.11f, -0.89f, -2.0f, -2.0f, -2.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineTo(4.0f)
        curveToRelative(0.0f, -1.11f, -0.89f, -2.0f, -2.0f, -2.0f)
        horizontalLineToRelative(-4.0f)
        curveToRelative(-0.99f, 0.0f, -1.8f, 0.7f, -1.96f, 1.64f)
        lineTo(10.0f, 5.6f)
        verticalLineTo(4.0f)
        close()
        moveTo(3.4f, 1.84f)
        lineTo(1.99f, 3.25f)
        lineTo(4.74f, 6.0f)
        horizontalLineTo(4.0f)
        curveToRelative(-1.11f, 0.0f, -1.99f, 0.89f, -1.99f, 2.0f)
        lineTo(2.0f, 19.0f)
        curveToRelative(0.0f, 1.11f, 0.89f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(15.74f)
        lineToRelative(2.0f, 2.0f)
        lineToRelative(1.41f, -1.41f)
        lineTo(3.4f, 1.84f)
        close()
        moveTo(4.0f, 19.0f)
        verticalLineTo(8.0f)
        horizontalLineToRelative(2.74f)
        lineToRelative(11.0f, 11.0f)
        horizontalLineTo(4.0f)
        close()
    }
}
