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

val Icons.TwoTone.WbIridescent: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(7.0f, 11.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(2.0f)
        horizontalLineTo(7.0f)
        close()
    }
    path {
        moveTo(5.0f, 15.0f)
        horizontalLineToRelative(14.0f)
        lineTo(19.0f, 9.0f)
        lineTo(5.0f, 9.0f)
        verticalLineToRelative(6.0f)
        close()
        moveTo(7.0f, 11.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(2.0f)
        lineTo(7.0f, 13.0f)
        verticalLineToRelative(-2.0f)
        close()
        moveTo(11.0f, 1.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(17.25f, 5.39f)
        lineToRelative(1.41f, 1.41f)
        lineToRelative(1.8f, -1.79f)
        lineToRelative(-1.42f, -1.41f)
        close()
        moveTo(11.0f, 20.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(17.24f, 18.71f)
        lineToRelative(1.79f, 1.8f)
        lineToRelative(1.42f, -1.42f)
        lineToRelative(-1.8f, -1.79f)
        close()
        moveTo(5.34f, 6.805f)
        lineToRelative(-1.788f, -1.79f)
        lineTo(4.96f, 3.61f)
        lineToRelative(1.788f, 1.788f)
        close()
        moveTo(3.55f, 19.08f)
        lineToRelative(1.41f, 1.42f)
        lineToRelative(1.79f, -1.8f)
        lineToRelative(-1.41f, -1.41f)
        close()
    }
}
