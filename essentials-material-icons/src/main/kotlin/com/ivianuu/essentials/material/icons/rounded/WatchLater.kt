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

val Icons.Rounded.WatchLater: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 2.0f)
        curveTo(6.5f, 2.0f, 2.0f, 6.5f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.5f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.5f, 10.0f, -10.0f)
        reflectiveCurveTo(17.5f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(15.55f, 15.8f)
        lineToRelative(-4.08f, -2.51f)
        curveToRelative(-0.3f, -0.18f, -0.48f, -0.5f, -0.48f, -0.85f)
        lineTo(10.99f, 7.75f)
        curveToRelative(0.01f, -0.41f, 0.35f, -0.75f, 0.76f, -0.75f)
        curveToRelative(0.41f, 0.0f, 0.75f, 0.34f, 0.75f, 0.75f)
        verticalLineToRelative(4.45f)
        lineToRelative(3.84f, 2.31f)
        curveToRelative(0.36f, 0.22f, 0.48f, 0.69f, 0.26f, 1.05f)
        curveToRelative(-0.22f, 0.35f, -0.69f, 0.46f, -1.05f, 0.24f)
        close()
    }
}
