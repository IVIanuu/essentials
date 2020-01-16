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

val Icons.Filled.PersonAddDisabled: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(15.0f, 8.0f)
        moveToRelative(-4.0f, 0.0f)
        arcToRelative(4.0f, 4.0f, 0.0f, true, true, 8.0f, 0.0f)
        arcToRelative(4.0f, 4.0f, 0.0f, true, true, -8.0f, 0.0f)
    }
    path {
        moveTo(23.0f, 20.0f)
        verticalLineToRelative(-2.0f)
        curveToRelative(0.0f, -2.3f, -4.1f, -3.7f, -6.9f, -3.9f)
        lineToRelative(6.0f, 5.9f)
        horizontalLineToRelative(0.9f)
        close()
        moveTo(11.4f, 14.5f)
        curveTo(9.2f, 15.1f, 7.0f, 16.3f, 7.0f, 18.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(9.9f)
        lineToRelative(4.0f, 4.0f)
        lineToRelative(1.3f, -1.3f)
        lineToRelative(-21.0f, -20.9f)
        lineTo(0.0f, 3.1f)
        lineToRelative(4.0f, 4.0f)
        lineTo(4.0f, 10.0f)
        lineTo(1.0f, 10.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(3.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-3.0f)
        horizontalLineToRelative(2.9f)
        lineToRelative(2.5f, 2.5f)
        close()
        moveTo(6.0f, 10.0f)
        verticalLineToRelative(-0.9f)
        lineToRelative(0.9f, 0.9f)
        lineTo(6.0f, 10.0f)
        close()
    }
}
