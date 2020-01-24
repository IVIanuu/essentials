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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Outlined.LocalHospital: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(19.0f, 3.0f)
        lineTo(5.0f, 3.0f)
        curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
        lineTo(3.0f, 19.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(21.0f, 5.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(19.0f, 19.0f)
        lineTo(5.0f, 19.0f)
        lineTo(5.0f, 5.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(10.5f, 17.0f)
        horizontalLineToRelative(3.0f)
        verticalLineToRelative(-3.5f)
        lineTo(17.0f, 13.5f)
        verticalLineToRelative(-3.0f)
        horizontalLineToRelative(-3.5f)
        lineTo(13.5f, 7.0f)
        horizontalLineToRelative(-3.0f)
        verticalLineToRelative(3.5f)
        lineTo(7.0f, 10.5f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(3.5f)
        close()
    }
}
