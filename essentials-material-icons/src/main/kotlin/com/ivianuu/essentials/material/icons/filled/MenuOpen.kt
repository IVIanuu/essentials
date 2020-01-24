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

package com.ivianuu.essentials.material.icons.filled

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.MenuOpen: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(3.0f, 18.0f)
        horizontalLineToRelative(13.0f)
        verticalLineToRelative(-2.0f)
        lineTo(3.0f, 16.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(3.0f, 13.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(-2.0f)
        lineTo(3.0f, 11.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(3.0f, 6.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(13.0f)
        lineTo(16.0f, 6.0f)
        lineTo(3.0f, 6.0f)
        close()
        moveTo(21.0f, 15.59f)
        lineTo(17.42f, 12.0f)
        lineTo(21.0f, 8.41f)
        lineTo(19.59f, 7.0f)
        lineToRelative(-5.0f, 5.0f)
        lineToRelative(5.0f, 5.0f)
        lineTo(21.0f, 15.59f)
        close()
    }
}
