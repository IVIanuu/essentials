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

val Icons.Rounded.SentimentDissatisfied: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(15.5f, 9.5f)
        moveToRelative(-1.5f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, 3.0f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, -3.0f, 0.0f)
    }
    path {
        moveTo(8.5f, 9.5f)
        moveToRelative(-1.5f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, 3.0f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, -3.0f, 0.0f)
    }
    path {
        moveTo(11.99f, 2.0f)
        curveTo(6.47f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.47f, 10.0f, 9.99f, 10.0f)
        curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
        reflectiveCurveTo(17.52f, 2.0f, 11.99f, 2.0f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-4.42f, 0.0f, -8.0f, -3.58f, -8.0f, -8.0f)
        reflectiveCurveToRelative(3.58f, -8.0f, 8.0f, -8.0f)
        reflectiveCurveToRelative(8.0f, 3.58f, 8.0f, 8.0f)
        reflectiveCurveToRelative(-3.58f, 8.0f, -8.0f, 8.0f)
        close()
        moveTo(12.0f, 14.0f)
        curveToRelative(-1.9f, 0.0f, -3.63f, 0.97f, -4.65f, 2.58f)
        curveToRelative(-0.22f, 0.35f, -0.11f, 0.81f, 0.24f, 1.03f)
        curveToRelative(0.35f, 0.22f, 0.81f, 0.11f, 1.03f, -0.24f)
        curveToRelative(0.74f, -1.18f, 2.0f, -1.88f, 3.38f, -1.88f)
        reflectiveCurveToRelative(2.64f, 0.7f, 3.38f, 1.88f)
        curveToRelative(0.14f, 0.23f, 0.39f, 0.35f, 0.64f, 0.35f)
        curveToRelative(0.14f, 0.0f, 0.27f, -0.04f, 0.4f, -0.11f)
        curveToRelative(0.35f, -0.22f, 0.46f, -0.68f, 0.24f, -1.03f)
        curveTo(15.63f, 14.96f, 13.9f, 14.0f, 12.0f, 14.0f)
        close()
    }
}
