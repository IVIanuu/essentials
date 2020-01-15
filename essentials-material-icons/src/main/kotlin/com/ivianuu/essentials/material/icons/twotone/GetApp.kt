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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.GetApp: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(14.17f, 11.0f)
        horizontalLineTo(13.0f)
        verticalLineTo(5.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(6.0f)
        horizontalLineTo(9.83f)
        lineTo(12.0f, 13.17f)
        close()
    }
    path {
        moveTo(19.0f, 9.0f)
        horizontalLineToRelative(-4.0f)
        lineTo(15.0f, 3.0f)
        lineTo(9.0f, 3.0f)
        verticalLineToRelative(6.0f)
        lineTo(5.0f, 9.0f)
        lineToRelative(7.0f, 7.0f)
        lineToRelative(7.0f, -7.0f)
        close()
        moveTo(11.0f, 11.0f)
        lineTo(11.0f, 5.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(1.17f)
        lineTo(12.0f, 13.17f)
        lineTo(9.83f, 11.0f)
        lineTo(11.0f, 11.0f)
        close()
        moveTo(5.0f, 18.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(2.0f)
        lineTo(5.0f, 20.0f)
        close()
    }
}
