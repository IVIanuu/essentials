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

val Icons.TwoTone.Subscriptions: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(4.0f, 20.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(-8.0f)
        lineTo(4.0f, 12.0f)
        verticalLineToRelative(8.0f)
        close()
        moveTo(10.0f, 12.73f)
        lineTo(16.0f, 16.0f)
        lineToRelative(-6.0f, 3.26f)
        verticalLineToRelative(-6.53f)
        close()
    }
    path {
        moveTo(4.0f, 6.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(2.0f)
        lineTo(4.0f, 8.0f)
        close()
        moveTo(6.0f, 2.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(2.0f)
        lineTo(6.0f, 4.0f)
        close()
        moveTo(20.0f, 10.0f)
        lineTo(4.0f, 10.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(8.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineToRelative(-8.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(20.0f, 20.0f)
        lineTo(4.0f, 20.0f)
        verticalLineToRelative(-8.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(8.0f)
        close()
        moveTo(10.0f, 12.73f)
        verticalLineToRelative(6.53f)
        lineTo(16.0f, 16.0f)
        close()
    }
}
