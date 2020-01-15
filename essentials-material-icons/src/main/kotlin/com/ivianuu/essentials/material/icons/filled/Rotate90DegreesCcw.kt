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

val Icons.Filled.Rotate90DegreesCcw: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(7.34f, 6.41f)
        lineTo(0.86f, 12.9f)
        lineToRelative(6.49f, 6.48f)
        lineToRelative(6.49f, -6.48f)
        lineToRelative(-6.5f, -6.49f)
        close()
        moveTo(3.69f, 12.9f)
        lineToRelative(3.66f, -3.66f)
        lineTo(11.0f, 12.9f)
        lineToRelative(-3.66f, 3.66f)
        lineToRelative(-3.65f, -3.66f)
        close()
        moveTo(19.36f, 6.64f)
        curveTo(17.61f, 4.88f, 15.3f, 4.0f, 13.0f, 4.0f)
        lineTo(13.0f, 0.76f)
        lineTo(8.76f, 5.0f)
        lineTo(13.0f, 9.24f)
        lineTo(13.0f, 6.0f)
        curveToRelative(1.79f, 0.0f, 3.58f, 0.68f, 4.95f, 2.05f)
        curveToRelative(2.73f, 2.73f, 2.73f, 7.17f, 0.0f, 9.9f)
        curveTo(16.58f, 19.32f, 14.79f, 20.0f, 13.0f, 20.0f)
        curveToRelative(-0.97f, 0.0f, -1.94f, -0.21f, -2.84f, -0.61f)
        lineToRelative(-1.49f, 1.49f)
        curveTo(10.02f, 21.62f, 11.51f, 22.0f, 13.0f, 22.0f)
        curveToRelative(2.3f, 0.0f, 4.61f, -0.88f, 6.36f, -2.64f)
        curveToRelative(3.52f, -3.51f, 3.52f, -9.21f, 0.0f, -12.72f)
        close()
    }
}
