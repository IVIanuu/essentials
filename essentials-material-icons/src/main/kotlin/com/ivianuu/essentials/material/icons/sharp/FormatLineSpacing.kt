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

package com.ivianuu.essentials.material.icons.sharp

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Sharp.FormatLineSpacing: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(6.0f, 7.0f)
        horizontalLineToRelative(2.5f)
        lineTo(5.0f, 3.5f)
        lineTo(1.5f, 7.0f)
        lineTo(4.0f, 7.0f)
        verticalLineToRelative(10.0f)
        lineTo(1.5f, 17.0f)
        lineTo(5.0f, 20.5f)
        lineTo(8.5f, 17.0f)
        lineTo(6.0f, 17.0f)
        lineTo(6.0f, 7.0f)
        close()
        moveTo(10.0f, 5.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(12.0f)
        lineTo(22.0f, 5.0f)
        lineTo(10.0f, 5.0f)
        close()
        moveTo(10.0f, 19.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(-2.0f)
        lineTo(10.0f, 17.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(10.0f, 13.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(-2.0f)
        lineTo(10.0f, 11.0f)
        verticalLineToRelative(2.0f)
        close()
    }
}
