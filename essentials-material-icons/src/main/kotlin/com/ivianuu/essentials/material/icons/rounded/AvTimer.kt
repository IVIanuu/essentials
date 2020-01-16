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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Rounded.AvTimer: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 17.0f)
        moveToRelative(-1.0f, 0.0f)
        arcToRelative(1.0f, 1.0f, 0.0f, true, true, 2.0f, 0.0f)
        arcToRelative(1.0f, 1.0f, 0.0f, true, true, -2.0f, 0.0f)
    }
    path {
        moveTo(7.0f, 12.0f)
        moveToRelative(-1.0f, 0.0f)
        arcToRelative(1.0f, 1.0f, 0.0f, true, true, 2.0f, 0.0f)
        arcToRelative(1.0f, 1.0f, 0.0f, true, true, -2.0f, 0.0f)
    }
    path {
        moveTo(17.0f, 12.0f)
        moveToRelative(-1.0f, 0.0f)
        arcToRelative(1.0f, 1.0f, 0.0f, true, true, 2.0f, 0.0f)
        arcToRelative(1.0f, 1.0f, 0.0f, true, true, -2.0f, 0.0f)
    }
    path {
        moveTo(12.0f, 3.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(2.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-0.92f)
        curveToRelative(3.31f, 0.48f, 5.87f, 3.25f, 6.0f, 6.66f)
        curveToRelative(0.14f, 3.85f, -3.03f, 7.2f, -6.88f, 7.26f)
        curveTo(8.19f, 19.06f, 5.0f, 15.91f, 5.0f, 12.0f)
        curveToRelative(0.0f, -1.68f, 0.59f, -3.22f, 1.58f, -4.42f)
        lineToRelative(4.71f, 4.72f)
        curveToRelative(0.39f, 0.39f, 1.02f, 0.39f, 1.41f, 0.0f)
        curveToRelative(0.39f, -0.39f, 0.39f, -1.02f, 0.0f, -1.41f)
        lineTo(7.26f, 5.46f)
        curveToRelative(-0.38f, -0.38f, -1.0f, -0.39f, -1.4f, -0.02f)
        curveTo(4.1f, 7.07f, 3.0f, 9.4f, 3.0f, 12.0f)
        curveToRelative(0.0f, 5.04f, 4.14f, 9.12f, 9.21f, 9.0f)
        curveToRelative(4.7f, -0.11f, 8.63f, -4.01f, 8.78f, -8.71f)
        curveTo(21.16f, 7.19f, 17.07f, 3.0f, 12.0f, 3.0f)
        close()
    }
}
