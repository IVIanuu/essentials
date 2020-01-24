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

val Icons.TwoTone.Duo: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.0f, 2.0f)
        horizontalLineToRelative(-8.0f)
        curveTo(6.38f, 2.0f, 2.0f, 6.66f, 2.0f, 12.28f)
        curveTo(2.0f, 17.5f, 6.49f, 22.0f, 11.72f, 22.0f)
        curveTo(17.39f, 22.0f, 22.0f, 17.62f, 22.0f, 12.0f)
        lineTo(22.0f, 4.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(17.0f, 15.0f)
        lineToRelative(-3.0f, -2.0f)
        verticalLineToRelative(2.0f)
        lineTo(7.0f, 15.0f)
        lineTo(7.0f, 9.0f)
        horizontalLineToRelative(7.0f)
        verticalLineToRelative(2.0f)
        lineToRelative(3.0f, -2.0f)
        verticalLineToRelative(6.0f)
        close()
    }
}
