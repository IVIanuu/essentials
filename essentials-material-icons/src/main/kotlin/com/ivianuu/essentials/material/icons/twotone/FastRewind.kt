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

val Icons.TwoTone.FastRewind: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(9.0f, 14.14f)
        lineTo(9.0f, 9.86f)
        lineTo(5.97f, 12.0f)
        close()
        moveTo(18.0f, 14.14f)
        lineTo(18.0f, 9.86f)
        lineTo(14.97f, 12.0f)
        close()
    }
    path {
        moveTo(11.0f, 6.0f)
        lineToRelative(-8.5f, 6.0f)
        lineToRelative(8.5f, 6.0f)
        lineTo(11.0f, 6.0f)
        close()
        moveTo(9.0f, 14.14f)
        lineTo(5.97f, 12.0f)
        lineTo(9.0f, 9.86f)
        verticalLineToRelative(4.28f)
        close()
        moveTo(20.0f, 6.0f)
        lineToRelative(-8.5f, 6.0f)
        lineToRelative(8.5f, 6.0f)
        lineTo(20.0f, 6.0f)
        close()
        moveTo(18.0f, 14.14f)
        lineTo(14.97f, 12.0f)
        lineTo(18.0f, 9.86f)
        verticalLineToRelative(4.28f)
        close()
    }
}
