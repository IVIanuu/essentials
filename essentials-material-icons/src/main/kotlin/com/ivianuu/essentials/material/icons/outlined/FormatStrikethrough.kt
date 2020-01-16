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

val Icons.Outlined.FormatStrikethrough: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(10.0f, 19.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(-3.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(3.0f)
        close()
        moveTo(5.0f, 4.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(4.0f)
        verticalLineTo(7.0f)
        horizontalLineToRelative(5.0f)
        verticalLineTo(4.0f)
        horizontalLineTo(5.0f)
        close()
        moveTo(3.0f, 14.0f)
        horizontalLineToRelative(18.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineTo(3.0f)
        verticalLineToRelative(2.0f)
        close()
    }
}
