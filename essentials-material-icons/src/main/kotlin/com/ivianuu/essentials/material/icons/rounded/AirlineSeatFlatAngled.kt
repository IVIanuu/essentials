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

import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path
import androidx.ui.graphics.vector.VectorAsset

val Icons.Rounded.AirlineSeatFlatAngled: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(22.25f, 14.29f)
        lineToRelative(-0.69f, 1.89f)
        lineTo(9.2f, 11.71f)
        lineToRelative(1.39f, -3.79f)
        curveToRelative(0.38f, -1.03f, 1.52f, -1.56f, 2.56f, -1.19f)
        lineToRelative(6.69f, 2.41f)
        curveToRelative(2.1f, 0.76f, 3.18f, 3.06f, 2.41f, 5.15f)
        close()
        moveTo(2.45f, 12.48f)
        lineToRelative(5.55f, 2.0f)
        lineTo(8.0f, 18.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(6.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-0.63f)
        lineToRelative(3.58f, 1.29f)
        curveToRelative(0.52f, 0.19f, 1.1f, -0.08f, 1.29f, -0.6f)
        curveToRelative(0.19f, -0.52f, -0.08f, -1.1f, -0.6f, -1.29f)
        lineTo(3.13f, 10.59f)
        curveToRelative(-0.52f, -0.19f, -1.1f, 0.08f, -1.29f, 0.6f)
        curveToRelative(-0.18f, 0.52f, 0.09f, 1.1f, 0.61f, 1.29f)
        close()
        moveTo(7.3f, 10.2f)
        curveToRelative(1.49f, -0.72f, 2.12f, -2.51f, 1.41f, -4.0f)
        curveTo(7.99f, 4.71f, 6.2f, 4.08f, 4.7f, 4.8f)
        curveToRelative(-1.49f, 0.71f, -2.12f, 2.5f, -1.4f, 4.0f)
        curveToRelative(0.71f, 1.49f, 2.5f, 2.12f, 4.0f, 1.4f)
        close()
    }
}
