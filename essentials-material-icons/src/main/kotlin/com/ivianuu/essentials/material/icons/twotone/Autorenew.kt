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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.TwoTone.Autorenew: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 6.0f)
        verticalLineToRelative(3.0f)
        lineToRelative(4.0f, -4.0f)
        lineToRelative(-4.0f, -4.0f)
        verticalLineToRelative(3.0f)
        curveToRelative(-4.42f, 0.0f, -8.0f, 3.58f, -8.0f, 8.0f)
        curveToRelative(0.0f, 1.57f, 0.46f, 3.03f, 1.24f, 4.26f)
        lineTo(6.7f, 14.8f)
        curveToRelative(-0.45f, -0.83f, -0.7f, -1.79f, -0.7f, -2.8f)
        curveToRelative(0.0f, -3.31f, 2.69f, -6.0f, 6.0f, -6.0f)
        close()
        moveTo(18.76f, 7.74f)
        lineTo(17.3f, 9.2f)
        curveToRelative(0.44f, 0.84f, 0.7f, 1.79f, 0.7f, 2.8f)
        curveToRelative(0.0f, 3.31f, -2.69f, 6.0f, -6.0f, 6.0f)
        verticalLineToRelative(-3.0f)
        lineToRelative(-4.0f, 4.0f)
        lineToRelative(4.0f, 4.0f)
        verticalLineToRelative(-3.0f)
        curveToRelative(4.42f, 0.0f, 8.0f, -3.58f, 8.0f, -8.0f)
        curveToRelative(0.0f, -1.57f, -0.46f, -3.03f, -1.24f, -4.26f)
        close()
    }
}
