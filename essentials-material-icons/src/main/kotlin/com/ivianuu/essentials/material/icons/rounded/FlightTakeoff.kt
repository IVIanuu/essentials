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

val Icons.Rounded.FlightTakeoff: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.5f, 19.0f)
        horizontalLineToRelative(-17.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        reflectiveCurveToRelative(0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(17.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
        close()
        moveTo(22.07f, 9.64f)
        curveToRelative(-0.22f, -0.8f, -1.04f, -1.27f, -1.84f, -1.06f)
        lineTo(14.92f, 10.0f)
        lineTo(8.46f, 3.98f)
        curveToRelative(-0.27f, -0.26f, -0.66f, -0.35f, -1.02f, -0.25f)
        curveToRelative(-0.68f, 0.19f, -1.0f, 0.97f, -0.65f, 1.58f)
        lineToRelative(3.44f, 5.96f)
        lineToRelative(-4.97f, 1.33f)
        lineToRelative(-1.57f, -1.24f)
        curveToRelative(-0.25f, -0.19f, -0.57f, -0.26f, -0.88f, -0.18f)
        lineToRelative(-0.33f, 0.09f)
        curveToRelative(-0.32f, 0.08f, -0.47f, 0.45f, -0.3f, 0.73f)
        lineToRelative(1.88f, 3.25f)
        curveToRelative(0.23f, 0.39f, 0.69f, 0.58f, 1.12f, 0.47f)
        lineTo(21.0f, 11.48f)
        curveToRelative(0.8f, -0.22f, 1.28f, -1.04f, 1.07f, -1.84f)
        close()
    }
}
