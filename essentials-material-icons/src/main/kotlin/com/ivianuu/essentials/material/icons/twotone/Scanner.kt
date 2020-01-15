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

val Icons.TwoTone.Scanner: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(5.0f, 14.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(-4.0f)
        lineTo(5.0f, 14.0f)
        close()
        moveTo(8.0f, 17.0f)
        lineTo(6.0f, 17.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(18.0f, 17.0f)
        horizontalLineToRelative(-8.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(2.0f)
        close()
    }
    path {
        moveTo(19.8f, 10.7f)
        lineTo(4.2f, 5.0f)
        lineToRelative(-0.7f, 1.9f)
        lineTo(17.6f, 12.0f)
        lineTo(5.0f, 12.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(4.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineToRelative(-5.5f)
        curveToRelative(0.0f, -0.8f, -0.5f, -1.6f, -1.2f, -1.8f)
        close()
        moveTo(19.0f, 18.0f)
        lineTo(5.0f, 18.0f)
        verticalLineToRelative(-4.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(4.0f)
        close()
        moveTo(6.0f, 15.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        lineTo(6.0f, 17.0f)
        close()
        moveTo(10.0f, 15.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-8.0f)
        close()
    }
}
