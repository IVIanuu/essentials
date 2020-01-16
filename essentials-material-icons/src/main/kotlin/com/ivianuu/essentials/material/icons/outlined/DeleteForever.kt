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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Outlined.DeleteForever: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(14.12f, 10.47f)
        lineTo(12.0f, 12.59f)
        lineToRelative(-2.13f, -2.12f)
        lineToRelative(-1.41f, 1.41f)
        lineTo(10.59f, 14.0f)
        lineToRelative(-2.12f, 2.12f)
        lineToRelative(1.41f, 1.41f)
        lineTo(12.0f, 15.41f)
        lineToRelative(2.12f, 2.12f)
        lineToRelative(1.41f, -1.41f)
        lineTo(13.41f, 14.0f)
        lineToRelative(2.12f, -2.12f)
        close()
        moveTo(15.5f, 4.0f)
        lineToRelative(-1.0f, -1.0f)
        horizontalLineToRelative(-5.0f)
        lineToRelative(-1.0f, 1.0f)
        horizontalLineTo(5.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(14.0f)
        verticalLineTo(4.0f)
        close()
        moveTo(6.0f, 19.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(8.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineTo(7.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(12.0f)
        close()
        moveTo(8.0f, 9.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(10.0f)
        horizontalLineTo(8.0f)
        verticalLineTo(9.0f)
        close()
    }
}
