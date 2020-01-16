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

package com.ivianuu.essentials.material.icons.twotone

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.TwoTone.PanoramaHorizontal: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(4.0f, 6.54f)
        verticalLineToRelative(10.91f)
        curveToRelative(2.6f, -0.77f, 5.28f, -1.16f, 8.0f, -1.16f)
        reflectiveCurveToRelative(5.4f, 0.39f, 8.0f, 1.16f)
        verticalLineTo(6.54f)
        curveToRelative(-2.6f, 0.78f, -5.28f, 1.17f, -8.0f, 1.16f)
        curveToRelative(-2.72f, 0.0f, -5.4f, -0.39f, -8.0f, -1.16f)
        close()
    }
    path {
        moveTo(21.43f, 4.0f)
        curveToRelative(-0.1f, 0.0f, -0.2f, 0.02f, -0.31f, 0.06f)
        curveTo(18.18f, 5.16f, 15.09f, 5.7f, 12.0f, 5.7f)
        reflectiveCurveToRelative(-6.18f, -0.55f, -9.12f, -1.64f)
        curveTo(2.77f, 4.02f, 2.66f, 4.0f, 2.57f, 4.0f)
        curveToRelative(-0.34f, 0.0f, -0.57f, 0.23f, -0.57f, 0.63f)
        verticalLineToRelative(14.75f)
        curveToRelative(0.0f, 0.39f, 0.23f, 0.62f, 0.57f, 0.62f)
        curveToRelative(0.1f, 0.0f, 0.2f, -0.02f, 0.31f, -0.06f)
        curveToRelative(2.94f, -1.1f, 6.03f, -1.64f, 9.12f, -1.64f)
        reflectiveCurveToRelative(6.18f, 0.55f, 9.12f, 1.64f)
        curveToRelative(0.11f, 0.04f, 0.21f, 0.06f, 0.31f, 0.06f)
        curveToRelative(0.33f, 0.0f, 0.57f, -0.23f, 0.57f, -0.63f)
        verticalLineTo(4.63f)
        curveToRelative(0.0f, -0.4f, -0.24f, -0.63f, -0.57f, -0.63f)
        close()
        moveTo(20.0f, 17.45f)
        curveToRelative(-2.6f, -0.77f, -5.28f, -1.16f, -8.0f, -1.16f)
        reflectiveCurveToRelative(-5.4f, 0.39f, -8.0f, 1.16f)
        verticalLineTo(6.54f)
        curveToRelative(2.6f, 0.77f, 5.28f, 1.16f, 8.0f, 1.16f)
        curveToRelative(2.72f, 0.01f, 5.4f, -0.38f, 8.0f, -1.16f)
        verticalLineToRelative(10.91f)
        close()
    }
}
