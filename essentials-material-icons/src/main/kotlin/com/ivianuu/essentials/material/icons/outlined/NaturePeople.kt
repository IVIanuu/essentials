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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Outlined.NaturePeople: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(4.5f, 9.5f)
        moveToRelative(-1.5f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, 3.0f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, -3.0f, 0.0f)
    }
    path {
        moveTo(22.17f, 9.17f)
        curveToRelative(0.0f, -3.87f, -3.13f, -7.0f, -7.0f, -7.0f)
        reflectiveCurveToRelative(-7.0f, 3.13f, -7.0f, 7.0f)
        curveToRelative(0.0f, 3.47f, 2.52f, 6.34f, 5.83f, 6.89f)
        lineTo(14.0f, 20.0f)
        lineTo(6.0f, 20.0f)
        verticalLineToRelative(-3.0f)
        horizontalLineToRelative(1.0f)
        verticalLineToRelative(-4.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        lineTo(3.0f, 12.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(1.0f)
        verticalLineToRelative(5.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(-3.0f)
        verticalLineToRelative(-3.88f)
        curveToRelative(3.47f, -0.41f, 6.17f, -3.36f, 6.17f, -6.95f)
        close()
        moveTo(15.17f, 14.17f)
        curveToRelative(-2.76f, 0.0f, -5.0f, -2.24f, -5.0f, -5.0f)
        reflectiveCurveToRelative(2.24f, -5.0f, 5.0f, -5.0f)
        reflectiveCurveToRelative(5.0f, 2.24f, 5.0f, 5.0f)
        reflectiveCurveToRelative(-2.24f, 5.0f, -5.0f, 5.0f)
        close()
    }
}
