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

package com.ivianuu.essentials.material.icons.filled

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.Unsubscribe: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.5f, 13.0f)
        curveToRelative(-1.93f, 0.0f, -3.5f, 1.57f, -3.5f, 3.5f)
        reflectiveCurveToRelative(1.57f, 3.5f, 3.5f, 3.5f)
        reflectiveCurveToRelative(3.5f, -1.57f, 3.5f, -3.5f)
        reflectiveCurveToRelative(-1.57f, -3.5f, -3.5f, -3.5f)
        close()
        moveTo(20.5f, 17.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(-1.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(1.0f)
        close()
        moveTo(13.55f, 17.0f)
        curveToRelative(-0.02f, -0.17f, -0.05f, -0.33f, -0.05f, -0.5f)
        curveToRelative(0.0f, -2.76f, 2.24f, -5.0f, 5.0f, -5.0f)
        curveToRelative(0.92f, 0.0f, 1.76f, 0.26f, 2.5f, 0.69f)
        lineTo(21.0f, 5.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        lineTo(5.0f, 3.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(10.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(8.55f)
        close()
        moveTo(12.0f, 10.5f)
        lineTo(5.0f, 7.0f)
        lineTo(5.0f, 5.0f)
        lineToRelative(7.0f, 3.5f)
        lineTo(19.0f, 5.0f)
        verticalLineToRelative(2.0f)
        lineToRelative(-7.0f, 3.5f)
        close()
    }
}
