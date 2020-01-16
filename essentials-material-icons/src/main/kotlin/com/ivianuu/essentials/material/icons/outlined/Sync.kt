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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Outlined.Sync: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 4.0f)
        lineTo(12.0f, 1.0f)
        lineTo(8.0f, 5.0f)
        lineToRelative(4.0f, 4.0f)
        lineTo(12.0f, 6.0f)
        curveToRelative(3.31f, 0.0f, 6.0f, 2.69f, 6.0f, 6.0f)
        curveToRelative(0.0f, 1.01f, -0.25f, 1.97f, -0.7f, 2.8f)
        lineToRelative(1.46f, 1.46f)
        curveTo(19.54f, 15.03f, 20.0f, 13.57f, 20.0f, 12.0f)
        curveToRelative(0.0f, -4.42f, -3.58f, -8.0f, -8.0f, -8.0f)
        close()
        moveTo(12.0f, 18.0f)
        curveToRelative(-3.31f, 0.0f, -6.0f, -2.69f, -6.0f, -6.0f)
        curveToRelative(0.0f, -1.01f, 0.25f, -1.97f, 0.7f, -2.8f)
        lineTo(5.24f, 7.74f)
        curveTo(4.46f, 8.97f, 4.0f, 10.43f, 4.0f, 12.0f)
        curveToRelative(0.0f, 4.42f, 3.58f, 8.0f, 8.0f, 8.0f)
        verticalLineToRelative(3.0f)
        lineToRelative(4.0f, -4.0f)
        lineToRelative(-4.0f, -4.0f)
        verticalLineToRelative(3.0f)
        close()
    }
}
