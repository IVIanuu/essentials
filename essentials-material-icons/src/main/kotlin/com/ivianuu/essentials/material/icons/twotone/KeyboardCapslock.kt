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

val Icons.TwoTone.KeyboardCapslock: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 8.41f)
        lineTo(16.59f, 13.0f)
        lineTo(18.0f, 11.59f)
        lineToRelative(-6.0f, -6.0f)
        lineToRelative(-6.0f, 6.0f)
        lineTo(7.41f, 13.0f)
        lineTo(12.0f, 8.41f)
        close()
        moveTo(6.0f, 18.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(2.0f)
        close()
    }
}
