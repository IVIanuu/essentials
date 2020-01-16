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

val Icons.Outlined.Gavel: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(1.0f, 21.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(2.0f)
        horizontalLineTo(1.0f)
        verticalLineToRelative(-2.0f)
        close()
        moveTo(5.24f, 8.07f)
        lineToRelative(2.83f, -2.83f)
        lineToRelative(14.14f, 14.14f)
        lineToRelative(-2.83f, 2.83f)
        lineTo(5.24f, 8.07f)
        close()
        moveTo(12.32f, 1.0f)
        lineToRelative(5.66f, 5.66f)
        lineToRelative(-2.83f, 2.83f)
        lineToRelative(-5.66f, -5.66f)
        lineTo(12.32f, 1.0f)
        close()
        moveTo(3.83f, 9.48f)
        lineToRelative(5.66f, 5.66f)
        lineToRelative(-2.83f, 2.83f)
        lineTo(1.0f, 12.31f)
        lineToRelative(2.83f, -2.83f)
        close()
    }
}
