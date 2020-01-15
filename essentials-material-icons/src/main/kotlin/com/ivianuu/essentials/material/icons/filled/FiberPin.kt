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

val Icons.Filled.FiberPin: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(5.5f, 10.5f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(1.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(20.0f, 4.0f)
        lineTo(4.0f, 4.0f)
        curveToRelative(-1.11f, 0.0f, -1.99f, 0.89f, -1.99f, 2.0f)
        lineTo(2.0f, 18.0f)
        curveToRelative(0.0f, 1.11f, 0.89f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(1.11f, 0.0f, 2.0f, -0.89f, 2.0f, -2.0f)
        lineTo(22.0f, 6.0f)
        curveToRelative(0.0f, -1.11f, -0.89f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(9.0f, 11.5f)
        curveToRelative(0.0f, 0.85f, -0.65f, 1.5f, -1.5f, 1.5f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(2.0f)
        lineTo(4.0f, 15.0f)
        lineTo(4.0f, 9.0f)
        horizontalLineToRelative(3.5f)
        curveToRelative(0.85f, 0.0f, 1.5f, 0.65f, 1.5f, 1.5f)
        verticalLineToRelative(1.0f)
        close()
        moveTo(12.5f, 15.0f)
        lineTo(11.0f, 15.0f)
        lineTo(11.0f, 9.0f)
        horizontalLineToRelative(1.5f)
        verticalLineToRelative(6.0f)
        close()
        moveTo(20.0f, 15.0f)
        horizontalLineToRelative(-1.2f)
        lineToRelative(-2.55f, -3.5f)
        lineTo(16.25f, 15.0f)
        lineTo(15.0f, 15.0f)
        lineTo(15.0f, 9.0f)
        horizontalLineToRelative(1.25f)
        lineToRelative(2.5f, 3.5f)
        lineTo(18.75f, 9.0f)
        lineTo(20.0f, 9.0f)
        verticalLineToRelative(6.0f)
        close()
    }
}
