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

val Icons.TwoTone.VoiceChat: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(4.0f, 17.17f)
        lineTo(5.17f, 16.0f)
        horizontalLineTo(20.0f)
        verticalLineTo(4.0f)
        horizontalLineTo(4.0f)
        verticalLineToRelative(13.17f)
        close()
        moveTo(7.0f, 7.0f)
        horizontalLineToRelative(7.0f)
        verticalLineToRelative(2.4f)
        lineTo(17.0f, 7.0f)
        verticalLineToRelative(6.0f)
        lineToRelative(-3.0f, -2.4f)
        verticalLineTo(13.0f)
        horizontalLineTo(7.0f)
        verticalLineTo(7.0f)
        close()
    }
    path {
        moveTo(20.0f, 2.0f)
        lineTo(4.0f, 2.0f)
        curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
        lineTo(2.0f, 22.0f)
        lineToRelative(4.0f, -4.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(22.0f, 4.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(20.0f, 16.0f)
        lineTo(5.17f, 16.0f)
        lineTo(4.0f, 17.17f)
        lineTo(4.0f, 4.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(12.0f)
        close()
        moveTo(14.0f, 10.6f)
        lineToRelative(3.0f, 2.4f)
        lineTo(17.0f, 7.0f)
        lineToRelative(-3.0f, 2.4f)
        lineTo(14.0f, 7.0f)
        lineTo(7.0f, 7.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(7.0f)
        close()
    }
}
