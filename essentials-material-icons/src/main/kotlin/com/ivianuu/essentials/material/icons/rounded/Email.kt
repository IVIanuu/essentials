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

val Icons.Rounded.Email: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.0f, 4.0f)
        lineTo(4.0f, 4.0f)
        curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
        lineTo(2.0f, 18.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(22.0f, 6.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(19.6f, 8.25f)
        lineToRelative(-7.07f, 4.42f)
        curveToRelative(-0.32f, 0.2f, -0.74f, 0.2f, -1.06f, 0.0f)
        lineTo(4.4f, 8.25f)
        curveToRelative(-0.25f, -0.16f, -0.4f, -0.43f, -0.4f, -0.72f)
        curveToRelative(0.0f, -0.67f, 0.73f, -1.07f, 1.3f, -0.72f)
        lineTo(12.0f, 11.0f)
        lineToRelative(6.7f, -4.19f)
        curveToRelative(0.57f, -0.35f, 1.3f, 0.05f, 1.3f, 0.72f)
        curveToRelative(0.0f, 0.29f, -0.15f, 0.56f, -0.4f, 0.72f)
        close()
    }
}
