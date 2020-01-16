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

val Icons.TwoTone.Publish: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(9.83f, 12.0f)
        horizontalLineTo(11.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-6.0f)
        horizontalLineToRelative(1.17f)
        lineTo(12.0f, 9.83f)
        close()
    }
    path {
        moveTo(5.0f, 4.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(2.0f)
        lineTo(5.0f, 6.0f)
        close()
        moveTo(12.0f, 7.0f)
        lineToRelative(-7.0f, 7.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(6.0f)
        verticalLineToRelative(-6.0f)
        horizontalLineToRelative(4.0f)
        lineToRelative(-7.0f, -7.0f)
        close()
        moveTo(13.0f, 12.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-6.0f)
        lineTo(9.83f, 12.0f)
        lineTo(12.0f, 9.83f)
        lineTo(14.17f, 12.0f)
        lineTo(13.0f, 12.0f)
        close()
    }
}
