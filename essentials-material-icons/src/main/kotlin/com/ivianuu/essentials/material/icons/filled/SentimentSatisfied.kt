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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Filled.SentimentSatisfied: VectorAsset by lazyMaterialIcon {
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
        moveTo(12.0f, 16.0f)
        curveToRelative(-0.73f, 0.0f, -1.38f, -0.18f, -1.96f, -0.52f)
        curveToRelative(-0.12f, 0.14f, -0.86f, 0.98f, -1.01f, 1.15f)
        curveToRelative(0.86f, 0.55f, 1.87f, 0.87f, 2.97f, 0.87f)
        curveToRelative(1.11f, 0.0f, 2.12f, -0.33f, 2.98f, -0.88f)
        curveToRelative(-0.97f, -1.09f, -0.01f, -0.02f, -1.01f, -1.15f)
        curveToRelative(-0.59f, 0.35f, -1.24f, 0.53f, -1.97f, 0.53f)
        close()
    }
}
