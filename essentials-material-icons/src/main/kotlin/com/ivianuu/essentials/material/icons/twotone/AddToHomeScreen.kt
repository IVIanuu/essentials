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

import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.ui.vector.VectorAsset

val Icons.TwoTone.AddToHomeScreen: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.0f, 1.01f)
        lineTo(8.0f, 1.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(2.0f)
        verticalLineTo(5.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(14.0f)
        horizontalLineTo(8.0f)
        verticalLineToRelative(-1.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(3.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineTo(3.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -1.99f, -2.0f, -1.99f)
        close()
        moveTo(10.0f, 15.0f)
        horizontalLineToRelative(2.0f)
        verticalLineTo(8.0f)
        horizontalLineTo(5.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(3.59f)
        lineTo(3.0f, 15.59f)
        lineTo(4.41f, 17.0f)
        lineTo(10.0f, 11.41f)
        verticalLineTo(15.0f)
        close()
    }
}
