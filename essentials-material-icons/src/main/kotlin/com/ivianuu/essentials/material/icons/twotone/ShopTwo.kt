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

val Icons.TwoTone.ShopTwo: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(7.0f, 7.0f)
        verticalLineToRelative(9.0f)
        horizontalLineToRelative(14.0f)
        lineTo(21.0f, 7.0f)
        lineTo(7.0f, 7.0f)
        close()
        moveTo(12.0f, 15.0f)
        lineTo(12.0f, 8.0f)
        lineToRelative(5.5f, 3.0f)
        lineToRelative(-5.5f, 4.0f)
        close()
    }
    path {
        moveTo(3.0f, 9.0f)
        lineTo(1.0f, 9.0f)
        verticalLineToRelative(11.0f)
        curveToRelative(0.0f, 1.11f, 0.89f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(1.11f, 0.0f, 2.0f, -0.89f, 2.0f, -2.0f)
        lineTo(3.0f, 20.0f)
        lineTo(3.0f, 9.0f)
        close()
        moveTo(18.0f, 5.0f)
        lineTo(18.0f, 3.0f)
        curveToRelative(0.0f, -1.11f, -0.89f, -2.0f, -2.0f, -2.0f)
        horizontalLineToRelative(-4.0f)
        curveToRelative(-1.11f, 0.0f, -2.0f, 0.89f, -2.0f, 2.0f)
        verticalLineToRelative(2.0f)
        lineTo(5.0f, 5.0f)
        verticalLineToRelative(11.0f)
        curveToRelative(0.0f, 1.11f, 0.89f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(1.11f, 0.0f, 2.0f, -0.89f, 2.0f, -2.0f)
        lineTo(23.0f, 5.0f)
        horizontalLineToRelative(-5.0f)
        close()
        moveTo(12.0f, 3.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-4.0f)
        lineTo(12.0f, 3.0f)
        close()
        moveTo(21.0f, 16.0f)
        lineTo(7.0f, 16.0f)
        lineTo(7.0f, 7.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(9.0f)
        close()
        moveTo(12.0f, 15.0f)
        lineToRelative(5.5f, -4.0f)
        lineTo(12.0f, 8.0f)
        close()
    }
}
