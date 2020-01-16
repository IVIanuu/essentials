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

val Icons.Rounded.CallSplit: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(14.85f, 4.85f)
        lineToRelative(1.44f, 1.44f)
        lineToRelative(-2.88f, 2.88f)
        lineToRelative(1.42f, 1.42f)
        lineToRelative(2.88f, -2.88f)
        lineToRelative(1.44f, 1.44f)
        curveToRelative(0.31f, 0.31f, 0.85f, 0.09f, 0.85f, -0.36f)
        verticalLineTo(4.5f)
        curveToRelative(0.0f, -0.28f, -0.22f, -0.5f, -0.5f, -0.5f)
        horizontalLineToRelative(-4.29f)
        curveToRelative(-0.45f, 0.0f, -0.67f, 0.54f, -0.36f, 0.85f)
        close()
        moveTo(8.79f, 4.0f)
        horizontalLineTo(4.5f)
        curveToRelative(-0.28f, 0.0f, -0.5f, 0.22f, -0.5f, 0.5f)
        verticalLineToRelative(4.29f)
        curveToRelative(0.0f, 0.45f, 0.54f, 0.67f, 0.85f, 0.35f)
        lineTo(6.29f, 7.7f)
        lineTo(11.0f, 12.4f)
        verticalLineTo(19.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-7.0f)
        curveToRelative(0.0f, -0.26f, -0.11f, -0.52f, -0.29f, -0.71f)
        lineToRelative(-5.0f, -5.01f)
        lineToRelative(1.44f, -1.44f)
        curveToRelative(0.31f, -0.3f, 0.09f, -0.84f, -0.36f, -0.84f)
        close()
    }
}
