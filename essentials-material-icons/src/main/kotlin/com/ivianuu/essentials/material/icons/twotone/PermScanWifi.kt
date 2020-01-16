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

val Icons.TwoTone.PermScanWifi: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 5.0f)
        curveToRelative(-3.26f, 0.0f, -6.2f, 0.85f, -9.08f, 2.65f)
        lineTo(12.0f, 18.83f)
        lineToRelative(9.08f, -11.16f)
        curveTo(18.18f, 5.85f, 15.25f, 5.0f, 12.0f, 5.0f)
        close()
        moveTo(13.0f, 16.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-6.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(6.0f)
        close()
        moveTo(11.0f, 8.0f)
        lineTo(11.0f, 6.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
    path {
        moveTo(12.0f, 3.0f)
        curveTo(6.95f, 3.0f, 3.15f, 4.85f, 0.0f, 7.23f)
        lineTo(12.0f, 22.0f)
        lineTo(24.0f, 7.25f)
        curveTo(20.85f, 4.87f, 17.05f, 3.0f, 12.0f, 3.0f)
        close()
        moveTo(2.92f, 7.65f)
        curveTo(5.8f, 5.85f, 8.74f, 5.0f, 12.0f, 5.0f)
        curveToRelative(3.25f, 0.0f, 6.18f, 0.85f, 9.08f, 2.67f)
        lineTo(12.0f, 18.83f)
        lineTo(2.92f, 7.65f)
        close()
        moveTo(11.0f, 10.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(11.0f, 6.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
}
