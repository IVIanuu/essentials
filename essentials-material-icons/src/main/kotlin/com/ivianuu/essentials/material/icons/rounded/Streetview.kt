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

package com.ivianuu.essentials.material.icons.rounded

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.Streetview: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.56f, 14.33f)
        curveToRelative(-0.34f, 0.27f, -0.56f, 0.7f, -0.56f, 1.17f)
        verticalLineTo(21.0f)
        horizontalLineToRelative(7.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineToRelative(-5.98f)
        curveToRelative(-0.94f, -0.33f, -1.95f, -0.52f, -3.0f, -0.52f)
        curveToRelative(-2.03f, 0.0f, -3.93f, 0.7f, -5.44f, 1.83f)
        close()
    }
    path {
        moveTo(18.0f, 6.0f)
        moveToRelative(-5.0f, 0.0f)
        arcToRelative(5.0f, 5.0f, 0.0f, true, true, 10.0f, 0.0f)
        arcToRelative(5.0f, 5.0f, 0.0f, true, true, -10.0f, 0.0f)
    }
    path {
        moveTo(11.5f, 6.0f)
        curveToRelative(0.0f, -1.08f, 0.27f, -2.1f, 0.74f, -3.0f)
        horizontalLineTo(5.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 0.55f, 0.23f, 1.05f, 0.59f, 1.41f)
        lineToRelative(9.82f, -9.82f)
        curveTo(12.23f, 9.42f, 11.5f, 7.8f, 11.5f, 6.0f)
        close()
    }
}
