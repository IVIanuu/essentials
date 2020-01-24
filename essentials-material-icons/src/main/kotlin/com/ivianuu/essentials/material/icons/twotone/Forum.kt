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

val Icons.TwoTone.Forum: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(15.0f, 11.0f)
        verticalLineTo(4.0f)
        horizontalLineTo(4.0f)
        verticalLineToRelative(8.17f)
        lineTo(5.17f, 11.0f)
        horizontalLineTo(6.0f)
        close()
    }
    path {
        moveTo(16.0f, 13.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        lineTo(17.0f, 3.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        lineTo(3.0f, 2.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(14.0f)
        lineToRelative(4.0f, -4.0f)
        horizontalLineToRelative(10.0f)
        close()
        moveTo(4.0f, 12.17f)
        lineTo(4.0f, 4.0f)
        horizontalLineToRelative(11.0f)
        verticalLineToRelative(7.0f)
        lineTo(5.17f, 11.0f)
        lineTo(4.0f, 12.17f)
        close()
        moveTo(22.0f, 7.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(9.0f)
        lineTo(6.0f, 15.0f)
        verticalLineToRelative(2.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(11.0f)
        lineToRelative(4.0f, 4.0f)
        lineTo(22.0f, 7.0f)
        close()
    }
}
