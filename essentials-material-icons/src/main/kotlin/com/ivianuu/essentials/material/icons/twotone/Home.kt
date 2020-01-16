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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.Home: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 3.0f)
        lineTo(2.0f, 12.0f)
        horizontalLineToRelative(3.0f)
        verticalLineToRelative(8.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(-6.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(-8.0f)
        horizontalLineToRelative(3.0f)
        lineTo(12.0f, 3.0f)
        close()
        moveTo(17.0f, 18.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-6.0f)
        lineTo(9.0f, 12.0f)
        verticalLineToRelative(6.0f)
        lineTo(7.0f, 18.0f)
        verticalLineToRelative(-7.81f)
        lineToRelative(5.0f, -4.5f)
        lineToRelative(5.0f, 4.5f)
        lineTo(17.0f, 18.0f)
        close()
    }
    path(fillAlpha = 0.3f) {
        moveTo(7.0f, 10.19f)
        verticalLineTo(18.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-6.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-7.81f)
        lineToRelative(-5.0f, -4.5f)
        close()
    }
}
