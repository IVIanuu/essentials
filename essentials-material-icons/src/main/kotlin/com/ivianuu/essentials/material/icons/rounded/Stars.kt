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

val Icons.Rounded.Stars: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(11.99f, 2.0f)
        curveTo(6.47f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.47f, 10.0f, 9.99f, 10.0f)
        curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
        reflectiveCurveTo(17.52f, 2.0f, 11.99f, 2.0f)
        close()
        moveTo(15.22f, 17.39f)
        lineTo(12.0f, 15.45f)
        lineToRelative(-3.22f, 1.94f)
        curveToRelative(-0.38f, 0.23f, -0.85f, -0.11f, -0.75f, -0.54f)
        lineToRelative(0.85f, -3.66f)
        lineToRelative(-2.83f, -2.45f)
        curveToRelative(-0.33f, -0.29f, -0.15f, -0.84f, 0.29f, -0.88f)
        lineToRelative(3.74f, -0.32f)
        lineToRelative(1.46f, -3.45f)
        curveToRelative(0.17f, -0.41f, 0.75f, -0.41f, 0.92f, 0.0f)
        lineToRelative(1.46f, 3.44f)
        lineToRelative(3.74f, 0.32f)
        curveToRelative(0.44f, 0.04f, 0.62f, 0.59f, 0.28f, 0.88f)
        lineToRelative(-2.83f, 2.45f)
        lineToRelative(0.85f, 3.67f)
        curveToRelative(0.1f, 0.43f, -0.36f, 0.77f, -0.74f, 0.54f)
        close()
    }
}
