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

val Icons.Outlined.PhonelinkRing: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.1f, 7.7f)
        lineToRelative(-1.0f, 1.0f)
        curveToRelative(1.8f, 1.8f, 1.8f, 4.6f, 0.0f, 6.5f)
        lineToRelative(1.0f, 1.0f)
        curveToRelative(2.5f, -2.3f, 2.5f, -6.1f, 0.0f, -8.5f)
        close()
        moveTo(18.0f, 9.8f)
        lineToRelative(-1.0f, 1.0f)
        curveToRelative(0.5f, 0.7f, 0.5f, 1.6f, 0.0f, 2.3f)
        lineToRelative(1.0f, 1.0f)
        curveToRelative(1.2f, -1.2f, 1.2f, -3.0f, 0.0f, -4.3f)
        close()
        moveTo(14.0f, 1.0f)
        lineTo(4.0f, 1.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(18.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(16.0f, 3.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(14.0f, 20.0f)
        lineTo(4.0f, 20.0f)
        lineTo(4.0f, 4.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(16.0f)
        close()
    }
}
