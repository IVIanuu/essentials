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

package com.ivianuu.essentials.material.icons.rounded

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Rounded.Note: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(21.41f, 9.41f)
        lineToRelative(-4.83f, -4.83f)
        curveToRelative(-0.37f, -0.37f, -0.88f, -0.58f, -1.41f, -0.58f)
        horizontalLineTo(4.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(12.01f)
        curveToRelative(0.0f, 1.1f, 0.89f, 1.99f, 1.99f, 1.99f)
        horizontalLineTo(20.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineToRelative(-7.17f)
        curveToRelative(0.0f, -0.53f, -0.21f, -1.04f, -0.59f, -1.42f)
        close()
        moveTo(15.0f, 5.5f)
        lineToRelative(5.5f, 5.5f)
        horizontalLineTo(16.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        verticalLineTo(5.5f)
        close()
    }
}
