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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.MobileScreenShare: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(17.0f, 1.0f)
        lineTo(7.0f, 1.0f)
        curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
        verticalLineToRelative(18.0f)
        curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 1.99f, 2.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(19.0f, 3.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(17.0f, 19.0f)
        lineTo(7.0f, 19.0f)
        lineTo(7.0f, 5.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(12.8f, 13.22f)
        verticalLineToRelative(1.75f)
        lineToRelative(2.81f, -2.62f)
        curveToRelative(0.21f, -0.2f, 0.21f, -0.53f, 0.0f, -0.73f)
        lineTo(12.8f, 9.0f)
        verticalLineToRelative(1.7f)
        curveToRelative(-3.11f, 0.43f, -4.35f, 2.56f, -4.8f, 4.7f)
        curveToRelative(1.11f, -1.5f, 2.58f, -2.18f, 4.8f, -2.18f)
        close()
    }
}
