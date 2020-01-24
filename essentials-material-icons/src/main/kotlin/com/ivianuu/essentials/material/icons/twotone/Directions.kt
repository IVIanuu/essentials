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

val Icons.TwoTone.Directions: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(3.01f, 12.0f)
        lineToRelative(9.0f, 9.0f)
        lineTo(21.0f, 12.0f)
        lineToRelative(-9.0f, -9.0f)
        lineToRelative(-8.99f, 9.0f)
        close()
        moveTo(14.0f, 7.5f)
        lineToRelative(3.5f, 3.5f)
        lineToRelative(-3.5f, 3.5f)
        verticalLineTo(12.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(3.0f)
        horizontalLineTo(8.0f)
        verticalLineToRelative(-4.0f)
        curveToRelative(0.0f, -0.55f, 0.45f, -1.0f, 1.0f, -1.0f)
        horizontalLineToRelative(5.0f)
        verticalLineTo(7.5f)
        close()
    }
    path {
        moveTo(13.42f, 1.58f)
        curveToRelative(-0.75f, -0.75f, -2.07f, -0.76f, -2.83f, 0.0f)
        lineToRelative(-9.0f, 9.0f)
        curveToRelative(-0.78f, 0.78f, -0.78f, 2.04f, 0.0f, 2.82f)
        lineToRelative(9.0f, 9.0f)
        curveToRelative(0.39f, 0.39f, 0.9f, 0.58f, 1.41f, 0.58f)
        curveToRelative(0.51f, 0.0f, 1.02f, -0.19f, 1.41f, -0.58f)
        lineToRelative(8.99f, -8.99f)
        curveToRelative(0.78f, -0.76f, 0.79f, -2.03f, 0.02f, -2.82f)
        lineToRelative(-9.0f, -9.01f)
        close()
        moveTo(12.01f, 20.99f)
        lineToRelative(-9.0f, -9.0f)
        lineToRelative(9.0f, -9.0f)
        lineToRelative(9.0f, 9.0f)
        lineToRelative(-9.0f, 9.0f)
        close()
        moveTo(8.0f, 11.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-3.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(2.5f)
        lineToRelative(3.5f, -3.5f)
        lineTo(14.0f, 7.5f)
        lineTo(14.0f, 10.0f)
        lineTo(9.0f, 10.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        close()
    }
}
