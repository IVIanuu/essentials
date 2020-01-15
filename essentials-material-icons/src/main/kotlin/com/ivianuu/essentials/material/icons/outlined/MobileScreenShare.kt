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

package com.ivianuu.essentials.material.icons.outlined

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Outlined.MobileScreenShare: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(17.0f, 1.0f)
        lineTo(7.0f, 1.0f)
        curveToRelative(-1.1f, 0.0f, -1.99f, 0.85f, -1.99f, 1.95f)
        verticalLineToRelative(18.0f)
        curveTo(5.01f, 22.05f, 5.9f, 23.0f, 7.0f, 23.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.95f, 2.0f, -2.05f)
        verticalLineToRelative(-18.0f)
        curveTo(19.0f, 1.85f, 18.1f, 1.0f, 17.0f, 1.0f)
        close()
        moveTo(17.0f, 19.0f)
        lineTo(7.0f, 19.0f)
        lineTo(7.0f, 5.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(12.8f, 13.24f)
        verticalLineToRelative(1.75f)
        lineTo(16.0f, 12.0f)
        lineToRelative(-3.2f, -2.98f)
        verticalLineToRelative(1.7f)
        curveToRelative(-3.11f, 0.43f, -4.35f, 2.56f, -4.8f, 4.7f)
        curveToRelative(1.11f, -1.5f, 2.58f, -2.18f, 4.8f, -2.18f)
        close()
    }
}
