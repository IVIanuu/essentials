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

val Icons.Rounded.SwitchVideo: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.0f, 9.5f)
        lineTo(18.0f, 6.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        lineTo(3.0f, 5.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-3.5f)
        lineToRelative(2.29f, 2.29f)
        curveToRelative(0.63f, 0.63f, 1.71f, 0.18f, 1.71f, -0.71f)
        lineTo(22.0f, 7.91f)
        curveToRelative(0.0f, -0.89f, -1.08f, -1.34f, -1.71f, -0.71f)
        lineTo(18.0f, 9.5f)
        close()
        moveTo(13.0f, 15.5f)
        lineTo(13.0f, 13.0f)
        lineTo(7.0f, 13.0f)
        verticalLineToRelative(2.5f)
        lineToRelative(-3.15f, -3.15f)
        curveToRelative(-0.2f, -0.2f, -0.2f, -0.51f, 0.0f, -0.71f)
        lineTo(7.0f, 8.5f)
        lineTo(7.0f, 11.0f)
        horizontalLineToRelative(6.0f)
        lineTo(13.0f, 8.5f)
        lineToRelative(3.15f, 3.15f)
        curveToRelative(0.2f, 0.2f, 0.2f, 0.51f, 0.0f, 0.71f)
        lineTo(13.0f, 15.5f)
        close()
    }
}
