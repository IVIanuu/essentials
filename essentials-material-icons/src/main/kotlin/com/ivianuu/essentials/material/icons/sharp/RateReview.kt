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

val Icons.Sharp.RateReview: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(22.0f, 2.0f)
        lineTo(2.0f, 2.0f)
        verticalLineToRelative(20.0f)
        lineToRelative(4.0f, -4.0f)
        horizontalLineToRelative(16.0f)
        lineTo(22.0f, 2.0f)
        close()
        moveTo(6.0f, 14.0f)
        verticalLineToRelative(-2.47f)
        lineToRelative(6.88f, -6.88f)
        curveToRelative(0.2f, -0.2f, 0.51f, -0.2f, 0.71f, 0.0f)
        lineToRelative(1.77f, 1.77f)
        curveToRelative(0.2f, 0.2f, 0.2f, 0.51f, 0.0f, 0.71f)
        lineTo(8.47f, 14.0f)
        lineTo(6.0f, 14.0f)
        close()
        moveTo(18.0f, 14.0f)
        horizontalLineToRelative(-7.5f)
        lineToRelative(2.0f, -2.0f)
        lineTo(18.0f, 12.0f)
        verticalLineToRelative(2.0f)
        close()
    }
}
