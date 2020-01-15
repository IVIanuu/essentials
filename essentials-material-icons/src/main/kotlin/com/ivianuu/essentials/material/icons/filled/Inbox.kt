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

val Icons.Filled.Inbox: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(19.0f, 3.0f)
        lineTo(4.99f, 3.0f)
        curveToRelative(-1.11f, 0.0f, -1.98f, 0.89f, -1.98f, 2.0f)
        lineTo(3.0f, 19.0f)
        curveToRelative(0.0f, 1.1f, 0.88f, 2.0f, 1.99f, 2.0f)
        lineTo(19.0f, 21.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(21.0f, 5.0f)
        curveToRelative(0.0f, -1.11f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(19.0f, 15.0f)
        horizontalLineToRelative(-4.0f)
        curveToRelative(0.0f, 1.66f, -1.35f, 3.0f, -3.0f, 3.0f)
        reflectiveCurveToRelative(-3.0f, -1.34f, -3.0f, -3.0f)
        lineTo(4.99f, 15.0f)
        lineTo(4.99f, 5.0f)
        lineTo(19.0f, 5.0f)
        verticalLineToRelative(10.0f)
        close()
    }
}
