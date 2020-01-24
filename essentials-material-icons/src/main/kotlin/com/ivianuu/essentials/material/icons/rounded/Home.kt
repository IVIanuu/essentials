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

val Icons.Rounded.Home: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(10.0f, 19.0f)
        verticalLineToRelative(-5.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(5.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(3.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-7.0f)
        horizontalLineToRelative(1.7f)
        curveToRelative(0.46f, 0.0f, 0.68f, -0.57f, 0.33f, -0.87f)
        lineTo(12.67f, 3.6f)
        curveToRelative(-0.38f, -0.34f, -0.96f, -0.34f, -1.34f, 0.0f)
        lineToRelative(-8.36f, 7.53f)
        curveToRelative(-0.34f, 0.3f, -0.13f, 0.87f, 0.33f, 0.87f)
        horizontalLineTo(5.0f)
        verticalLineToRelative(7.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(3.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        close()
    }
}
