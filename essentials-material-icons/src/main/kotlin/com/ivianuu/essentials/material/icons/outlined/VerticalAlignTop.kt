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

val Icons.Outlined.VerticalAlignTop: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(8.0f, 11.0f)
        horizontalLineToRelative(3.0f)
        verticalLineToRelative(10.0f)
        horizontalLineToRelative(2.0f)
        verticalLineTo(11.0f)
        horizontalLineToRelative(3.0f)
        lineToRelative(-4.0f, -4.0f)
        lineToRelative(-4.0f, 4.0f)
        close()
        moveTo(4.0f, 3.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(16.0f)
        verticalLineTo(3.0f)
        horizontalLineTo(4.0f)
        close()
    }
}
