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

val Icons.Rounded.PhotoAlbum: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.0f, 2.0f)
        lineTo(6.0f, 2.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(16.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(20.0f, 4.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(6.0f, 4.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(8.0f)
        lineToRelative(-2.5f, -1.5f)
        lineTo(6.0f, 12.0f)
        lineTo(6.0f, 4.0f)
        close()
        moveTo(6.63f, 18.19f)
        lineToRelative(1.99f, -2.56f)
        curveToRelative(0.2f, -0.25f, 0.58f, -0.26f, 0.78f, -0.01f)
        lineToRelative(1.74f, 2.1f)
        lineToRelative(2.6f, -3.34f)
        curveToRelative(0.2f, -0.26f, 0.6f, -0.26f, 0.79f, 0.01f)
        lineToRelative(2.87f, 3.82f)
        curveToRelative(0.25f, 0.33f, 0.01f, 0.8f, -0.4f, 0.8f)
        lineTo(7.02f, 19.01f)
        curveToRelative(-0.41f, -0.01f, -0.65f, -0.49f, -0.39f, -0.82f)
        close()
    }
}
