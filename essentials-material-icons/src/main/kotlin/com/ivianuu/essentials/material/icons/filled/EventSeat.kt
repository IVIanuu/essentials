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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Filled.EventSeat: VectorAsset by lazyMaterialIcon {
    group {
        path {
            moveTo(4.0f, 18.0f)
            verticalLineToRelative(3.0f)
            horizontalLineToRelative(3.0f)
            verticalLineToRelative(-3.0f)
            horizontalLineToRelative(10.0f)
            verticalLineToRelative(3.0f)
            horizontalLineToRelative(3.0f)
            verticalLineToRelative(-6.0f)
            lineTo(4.0f, 15.0f)
            close()
            moveTo(19.0f, 10.0f)
            horizontalLineToRelative(3.0f)
            verticalLineToRelative(3.0f)
            horizontalLineToRelative(-3.0f)
            close()
            moveTo(2.0f, 10.0f)
            horizontalLineToRelative(3.0f)
            verticalLineToRelative(3.0f)
            lineTo(2.0f, 13.0f)
            close()
            moveTo(17.0f, 13.0f)
            lineTo(7.0f, 13.0f)
            lineTo(7.0f, 5.0f)
            curveToRelative(0.0f, -1.1f, 0.9f, -2.0f, 2.0f, -2.0f)
            horizontalLineToRelative(6.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
            verticalLineToRelative(8.0f)
            close()
        }
    }
}
