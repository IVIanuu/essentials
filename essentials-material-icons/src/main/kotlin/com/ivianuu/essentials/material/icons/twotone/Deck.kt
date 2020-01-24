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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.Deck: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f, strokeAlpha = 0.3f) {
        moveTo(12.0f, 4.44f)
        lineToRelative(-3.66f, 2.56f)
        lineToRelative(7.32f, 0.0f)
        close()
    }
    path {
        moveTo(22.0f, 9.0f)
        lineTo(12.0f, 2.0f)
        lineTo(2.0f, 9.0f)
        horizontalLineToRelative(9.0f)
        verticalLineToRelative(13.0f)
        horizontalLineToRelative(2.0f)
        verticalLineTo(9.0f)
        horizontalLineTo(22.0f)
        close()
        moveTo(12.0f, 4.44f)
        lineTo(15.66f, 7.0f)
        horizontalLineTo(8.34f)
        lineTo(12.0f, 4.44f)
        close()
    }
    path {
        moveTo(4.14f, 12.0f)
        lineToRelative(-1.96f, 0.37f)
        lineToRelative(0.82f, 4.37f)
        lineToRelative(0.0f, 5.26f)
        lineToRelative(2.0f, 0.0f)
        lineToRelative(0.02f, -4.0f)
        lineToRelative(1.98f, 0.0f)
        lineToRelative(0.0f, 4.0f)
        lineToRelative(2.0f, 0.0f)
        lineToRelative(0.0f, -6.0f)
        lineToRelative(-4.1f, 0.0f)
        close()
    }
    path {
        moveTo(19.1f, 16.0f)
        lineToRelative(-4.1f, 0.0f)
        lineToRelative(0.0f, 6.0f)
        lineToRelative(2.0f, 0.0f)
        lineToRelative(0.0f, -4.0f)
        lineToRelative(1.98f, 0.0f)
        lineToRelative(0.02f, 4.0f)
        lineToRelative(2.0f, 0.0f)
        lineToRelative(0.0f, -5.26f)
        lineToRelative(0.82f, -4.37f)
        lineToRelative(-1.96f, -0.37f)
        close()
    }
}
