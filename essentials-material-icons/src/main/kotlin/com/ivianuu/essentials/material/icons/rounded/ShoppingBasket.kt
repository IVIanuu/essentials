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

val Icons.Rounded.ShoppingBasket: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(22.0f, 9.0f)
        horizontalLineToRelative(-4.79f)
        lineToRelative(-4.39f, -6.57f)
        curveToRelative(-0.4f, -0.59f, -1.27f, -0.59f, -1.66f, 0.0f)
        lineTo(6.77f, 9.0f)
        horizontalLineTo(2.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        curveToRelative(0.0f, 0.09f, 0.01f, 0.18f, 0.04f, 0.27f)
        lineToRelative(2.54f, 9.27f)
        curveToRelative(0.23f, 0.84f, 1.0f, 1.46f, 1.92f, 1.46f)
        horizontalLineToRelative(13.0f)
        curveToRelative(0.92f, 0.0f, 1.69f, -0.62f, 1.93f, -1.46f)
        lineToRelative(2.54f, -9.27f)
        lineTo(23.0f, 10.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        close()
        moveTo(11.99f, 4.79f)
        lineTo(14.8f, 9.0f)
        horizontalLineTo(9.18f)
        lineToRelative(2.81f, -4.21f)
        close()
        moveTo(12.0f, 17.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, -0.9f, -2.0f, -2.0f)
        reflectiveCurveToRelative(0.9f, -2.0f, 2.0f, -2.0f)
        reflectiveCurveToRelative(2.0f, 0.9f, 2.0f, 2.0f)
        reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
        close()
    }
}
