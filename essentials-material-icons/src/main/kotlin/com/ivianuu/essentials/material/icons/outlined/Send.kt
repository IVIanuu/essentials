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

val Icons.Outlined.Send: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(4.01f, 6.03f)
        lineToRelative(7.51f, 3.22f)
        lineToRelative(-7.52f, -1.0f)
        lineToRelative(0.01f, -2.22f)
        moveToRelative(7.5f, 8.72f)
        lineTo(4.0f, 17.97f)
        verticalLineToRelative(-2.22f)
        lineToRelative(7.51f, -1.0f)
        moveTo(2.01f, 3.0f)
        lineTo(2.0f, 10.0f)
        lineToRelative(15.0f, 2.0f)
        lineToRelative(-15.0f, 2.0f)
        lineToRelative(0.01f, 7.0f)
        lineTo(23.0f, 12.0f)
        lineTo(2.01f, 3.0f)
        close()
    }
}
