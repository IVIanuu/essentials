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

val Icons.Rounded.NaturePeople: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(4.5f, 9.5f)
        moveToRelative(-1.5f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, 3.0f, 0.0f)
        arcToRelative(1.5f, 1.5f, 0.0f, true, true, -3.0f, 0.0f)
    }
    path {
        moveTo(22.17f, 9.17f)
        curveToRelative(0.0f, -3.91f, -3.19f, -7.06f, -7.11f, -7.0f)
        curveToRelative(-3.83f, 0.06f, -6.99f, 3.37f, -6.88f, 7.19f)
        curveToRelative(0.09f, 3.38f, 2.58f, 6.16f, 5.83f, 6.7f)
        verticalLineTo(20.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(-3.0f)
        horizontalLineToRelative(0.5f)
        curveToRelative(0.28f, 0.0f, 0.5f, -0.22f, 0.5f, -0.5f)
        verticalLineTo(13.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        horizontalLineTo(3.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(3.5f)
        curveToRelative(0.0f, 0.28f, 0.22f, 0.5f, 0.5f, 0.5f)
        horizontalLineTo(3.0f)
        verticalLineToRelative(4.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-3.88f)
        curveToRelative(3.47f, -0.41f, 6.17f, -3.36f, 6.17f, -6.95f)
        close()
    }
}
