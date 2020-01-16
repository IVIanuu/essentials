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

package com.ivianuu.essentials.material.icons.rounded

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.ArrowForwardIos: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(7.38f, 21.01f)
        curveToRelative(0.49f, 0.49f, 1.28f, 0.49f, 1.77f, 0.0f)
        lineToRelative(8.31f, -8.31f)
        curveToRelative(0.39f, -0.39f, 0.39f, -1.02f, 0.0f, -1.41f)
        lineTo(9.15f, 2.98f)
        curveToRelative(-0.49f, -0.49f, -1.28f, -0.49f, -1.77f, 0.0f)
        reflectiveCurveToRelative(-0.49f, 1.28f, 0.0f, 1.77f)
        lineTo(14.62f, 12.0f)
        lineToRelative(-7.25f, 7.25f)
        curveToRelative(-0.48f, 0.48f, -0.48f, 1.28f, 0.01f, 1.76f)
        close()
    }
}
