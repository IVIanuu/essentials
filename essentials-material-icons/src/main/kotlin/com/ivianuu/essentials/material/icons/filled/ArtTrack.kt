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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.ArtTrack: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(22.0f, 13.0f)
        horizontalLineToRelative(-8.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(22.0f, 7.0f)
        horizontalLineToRelative(-8.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(8.0f)
        lineTo(22.0f, 7.0f)
        close()
        moveTo(14.0f, 17.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(-8.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(12.0f, 9.0f)
        verticalLineToRelative(6.0f)
        curveToRelative(0.0f, 1.1f, -0.9f, 2.0f, -2.0f, 2.0f)
        lineTo(4.0f, 17.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, -0.9f, -2.0f, -2.0f)
        lineTo(2.0f, 9.0f)
        curveToRelative(0.0f, -1.1f, 0.9f, -2.0f, 2.0f, -2.0f)
        horizontalLineToRelative(6.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
        close()
        moveTo(10.5f, 15.0f)
        lineToRelative(-2.25f, -3.0f)
        lineToRelative(-1.75f, 2.26f)
        lineToRelative(-1.25f, -1.51f)
        lineTo(3.5f, 15.0f)
        horizontalLineToRelative(7.0f)
        close()
    }
}
