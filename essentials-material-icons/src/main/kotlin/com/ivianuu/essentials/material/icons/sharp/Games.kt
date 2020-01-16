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

val Icons.Sharp.Games: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(15.0f, 7.5f)
        verticalLineTo(2.0f)
        horizontalLineTo(9.0f)
        verticalLineToRelative(5.5f)
        lineToRelative(3.0f, 3.0f)
        lineToRelative(3.0f, -3.0f)
        close()
        moveTo(7.5f, 9.0f)
        horizontalLineTo(2.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(5.5f)
        lineToRelative(3.0f, -3.0f)
        lineToRelative(-3.0f, -3.0f)
        close()
        moveTo(9.0f, 16.5f)
        verticalLineTo(22.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(-5.5f)
        lineToRelative(-3.0f, -3.0f)
        lineToRelative(-3.0f, 3.0f)
        close()
        moveTo(16.5f, 9.0f)
        lineToRelative(-3.0f, 3.0f)
        lineToRelative(3.0f, 3.0f)
        horizontalLineTo(22.0f)
        verticalLineTo(9.0f)
        horizontalLineToRelative(-5.5f)
        close()
    }
}
