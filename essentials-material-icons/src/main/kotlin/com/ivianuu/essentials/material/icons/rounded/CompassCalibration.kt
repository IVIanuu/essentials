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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.CompassCalibration: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 17.0f)
        moveToRelative(-4.0f, 0.0f)
        arcToRelative(4.0f, 4.0f, 0.0f, true, true, 8.0f, 0.0f)
        arcToRelative(4.0f, 4.0f, 0.0f, true, true, -8.0f, 0.0f)
    }
    path {
        moveTo(12.0f, 3.0f)
        curveTo(8.49f, 3.0f, 5.28f, 4.29f, 2.8f, 6.41f)
        curveToRelative(-0.44f, 0.38f, -0.48f, 1.06f, -0.06f, 1.48f)
        lineToRelative(3.6f, 3.6f)
        curveToRelative(0.36f, 0.36f, 0.92f, 0.39f, 1.32f, 0.08f)
        curveToRelative(1.2f, -0.94f, 2.71f, -1.5f, 4.34f, -1.5f)
        curveToRelative(1.64f, 0.0f, 3.14f, 0.56f, 4.34f, 1.49f)
        curveToRelative(0.4f, 0.31f, 0.96f, 0.28f, 1.31f, -0.08f)
        lineToRelative(3.6f, -3.6f)
        curveToRelative(0.42f, -0.42f, 0.38f, -1.1f, -0.07f, -1.48f)
        curveTo(18.72f, 4.28f, 15.51f, 3.0f, 12.0f, 3.0f)
        close()
    }
}
