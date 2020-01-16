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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Filled.TextRotateUp: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(3.0f, 12.0f)
        verticalLineToRelative(1.5f)
        lineToRelative(11.0f, 4.75f)
        verticalLineToRelative(-2.1f)
        lineToRelative(-2.2f, -0.9f)
        verticalLineToRelative(-5.0f)
        lineToRelative(2.2f, -0.9f)
        verticalLineToRelative(-2.1f)
        lineTo(3.0f, 12.0f)
        close()
        moveTo(10.0f, 14.62f)
        lineToRelative(-5.02f, -1.87f)
        lineTo(10.0f, 10.88f)
        verticalLineToRelative(3.74f)
        close()
        moveTo(18.0f, 4.25f)
        lineToRelative(-3.0f, 3.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(12.5f)
        horizontalLineToRelative(2.0f)
        lineTo(19.0f, 7.25f)
        horizontalLineToRelative(2.0f)
        lineToRelative(-3.0f, -3.0f)
        close()
    }
}
