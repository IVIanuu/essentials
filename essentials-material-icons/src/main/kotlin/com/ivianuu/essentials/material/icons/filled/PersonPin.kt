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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.PersonPin: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.0f)
        curveToRelative(-4.97f, 0.0f, -9.0f, 4.03f, -9.0f, 9.0f)
        curveToRelative(0.0f, 4.17f, 2.84f, 7.67f, 6.69f, 8.69f)
        lineTo(12.0f, 22.0f)
        lineToRelative(2.31f, -2.31f)
        curveTo(18.16f, 18.67f, 21.0f, 15.17f, 21.0f, 11.0f)
        curveToRelative(0.0f, -4.97f, -4.03f, -9.0f, -9.0f, -9.0f)
        close()
        moveTo(12.0f, 4.0f)
        curveToRelative(1.66f, 0.0f, 3.0f, 1.34f, 3.0f, 3.0f)
        reflectiveCurveToRelative(-1.34f, 3.0f, -3.0f, 3.0f)
        reflectiveCurveToRelative(-3.0f, -1.34f, -3.0f, -3.0f)
        reflectiveCurveToRelative(1.34f, -3.0f, 3.0f, -3.0f)
        close()
        moveTo(12.0f, 18.3f)
        curveToRelative(-2.5f, 0.0f, -4.71f, -1.28f, -6.0f, -3.22f)
        curveToRelative(0.03f, -1.99f, 4.0f, -3.08f, 6.0f, -3.08f)
        curveToRelative(1.99f, 0.0f, 5.97f, 1.09f, 6.0f, 3.08f)
        curveToRelative(-1.29f, 1.94f, -3.5f, 3.22f, -6.0f, 3.22f)
        close()
    }
}
