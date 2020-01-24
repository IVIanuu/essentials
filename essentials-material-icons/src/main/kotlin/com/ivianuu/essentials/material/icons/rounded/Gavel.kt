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

val Icons.Rounded.Gavel: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(2.0f, 21.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
        reflectiveCurveToRelative(-0.45f, 1.0f, -1.0f, 1.0f)
        lineTo(2.0f, 23.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        reflectiveCurveToRelative(0.45f, -1.0f, 1.0f, -1.0f)
        close()
        moveTo(5.24f, 8.07f)
        lineToRelative(2.83f, -2.83f)
        lineTo(20.8f, 17.97f)
        curveToRelative(0.78f, 0.78f, 0.78f, 2.05f, 0.0f, 2.83f)
        curveToRelative(-0.78f, 0.78f, -2.05f, 0.78f, -2.83f, 0.0f)
        lineTo(5.24f, 8.07f)
        close()
        moveTo(13.73f, 2.41f)
        lineToRelative(2.83f, 2.83f)
        curveToRelative(0.78f, 0.78f, 0.78f, 2.05f, 0.0f, 2.83f)
        lineToRelative(-1.42f, 1.42f)
        lineToRelative(-5.65f, -5.66f)
        lineToRelative(1.41f, -1.41f)
        curveToRelative(0.78f, -0.79f, 2.05f, -0.79f, 2.83f, -0.01f)
        close()
        moveTo(3.83f, 9.48f)
        lineToRelative(5.66f, 5.66f)
        lineToRelative(-1.41f, 1.41f)
        curveToRelative(-0.78f, 0.78f, -2.05f, 0.78f, -2.83f, 0.0f)
        lineToRelative(-2.83f, -2.83f)
        curveToRelative(-0.78f, -0.78f, -0.78f, -2.05f, 0.0f, -2.83f)
        lineToRelative(1.41f, -1.41f)
        close()
    }
}
