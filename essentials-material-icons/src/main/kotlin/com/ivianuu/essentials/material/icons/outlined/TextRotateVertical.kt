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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Outlined.TextRotateVertical: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(15.75f, 5.0f)
        horizontalLineToRelative(-1.5f)
        lineTo(9.5f, 16.0f)
        horizontalLineToRelative(2.1f)
        lineToRelative(0.9f, -2.2f)
        horizontalLineToRelative(5.0f)
        lineToRelative(0.9f, 2.2f)
        horizontalLineToRelative(2.1f)
        lineTo(15.75f, 5.0f)
        close()
        moveTo(13.13f, 12.0f)
        lineTo(15.0f, 6.98f)
        lineTo(16.87f, 12.0f)
        horizontalLineToRelative(-3.74f)
        close()
        moveTo(6.0f, 20.0f)
        lineToRelative(3.0f, -3.0f)
        lineTo(7.0f, 17.0f)
        lineTo(7.0f, 4.0f)
        lineTo(5.0f, 4.0f)
        verticalLineToRelative(13.0f)
        lineTo(3.0f, 17.0f)
        lineToRelative(3.0f, 3.0f)
        close()
    }
}
