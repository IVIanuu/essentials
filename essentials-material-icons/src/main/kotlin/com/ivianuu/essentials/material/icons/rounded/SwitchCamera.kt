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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Rounded.SwitchCamera: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.0f, 4.0f)
        horizontalLineToRelative(-3.17f)
        lineToRelative(-1.24f, -1.35f)
        curveToRelative(-0.37f, -0.41f, -0.91f, -0.65f, -1.47f, -0.65f)
        lineTo(9.88f, 2.0f)
        curveToRelative(-0.56f, 0.0f, -1.1f, 0.24f, -1.48f, 0.65f)
        lineTo(7.17f, 4.0f)
        lineTo(4.0f, 4.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(22.0f, 6.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(15.0f, 15.5f)
        lineTo(15.0f, 13.0f)
        lineTo(9.0f, 13.0f)
        verticalLineToRelative(2.5f)
        lineToRelative(-3.15f, -3.15f)
        curveToRelative(-0.2f, -0.2f, -0.2f, -0.51f, 0.0f, -0.71f)
        lineTo(9.0f, 8.5f)
        lineTo(9.0f, 11.0f)
        horizontalLineToRelative(6.0f)
        lineTo(15.0f, 8.5f)
        lineToRelative(3.15f, 3.15f)
        curveToRelative(0.2f, 0.2f, 0.2f, 0.51f, 0.0f, 0.71f)
        lineTo(15.0f, 15.5f)
        close()
    }
}
