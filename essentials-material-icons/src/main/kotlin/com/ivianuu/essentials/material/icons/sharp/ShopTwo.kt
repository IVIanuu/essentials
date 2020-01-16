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

package com.ivianuu.essentials.material.icons.sharp

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Sharp.ShopTwo: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(3.0f, 9.0f)
        lineTo(1.0f, 9.0f)
        verticalLineToRelative(13.0f)
        horizontalLineToRelative(18.0f)
        verticalLineToRelative(-2.0f)
        lineTo(3.0f, 20.0f)
        lineTo(3.0f, 9.0f)
        close()
        moveTo(18.0f, 5.0f)
        lineTo(18.0f, 3.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        horizontalLineToRelative(-4.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(2.0f)
        lineTo(5.0f, 5.0f)
        verticalLineToRelative(13.0f)
        horizontalLineToRelative(18.0f)
        lineTo(23.0f, 5.0f)
        horizontalLineToRelative(-5.0f)
        close()
        moveTo(12.0f, 3.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-4.0f)
        lineTo(12.0f, 3.0f)
        close()
        moveTo(12.0f, 15.0f)
        lineTo(12.0f, 8.0f)
        lineToRelative(5.5f, 3.0f)
        lineToRelative(-5.5f, 4.0f)
        close()
    }
}
