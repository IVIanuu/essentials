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

val Icons.TwoTone.Colorize: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(15.896f, 9.023f)
        lineToRelative(-0.92f, -0.92f)
        lineTo(17.67f, 5.41f)
        lineToRelative(0.92f, 0.92f)
        close()
    }
    path {
        moveTo(20.71f, 5.63f)
        lineToRelative(-2.34f, -2.34f)
        curveToRelative(-0.2f, -0.2f, -0.45f, -0.29f, -0.71f, -0.29f)
        reflectiveCurveToRelative(-0.51f, 0.1f, -0.7f, 0.29f)
        lineToRelative(-3.12f, 3.12f)
        lineToRelative(-1.93f, -1.91f)
        lineToRelative(-1.41f, 1.41f)
        lineToRelative(1.42f, 1.42f)
        lineTo(3.0f, 16.25f)
        lineTo(3.0f, 21.0f)
        horizontalLineToRelative(4.75f)
        lineToRelative(8.92f, -8.92f)
        lineToRelative(1.42f, 1.42f)
        lineToRelative(1.41f, -1.41f)
        lineToRelative(-1.92f, -1.92f)
        lineToRelative(3.12f, -3.12f)
        curveToRelative(0.4f, -0.4f, 0.4f, -1.03f, 0.01f, -1.42f)
        close()
        moveTo(6.92f, 19.0f)
        lineTo(5.0f, 17.08f)
        lineToRelative(8.06f, -8.06f)
        lineToRelative(1.92f, 1.92f)
        lineTo(6.92f, 19.0f)
        close()
        moveTo(15.9f, 9.03f)
        lineToRelative(-0.93f, -0.93f)
        lineToRelative(2.69f, -2.69f)
        lineToRelative(0.92f, 0.92f)
        lineToRelative(-2.68f, 2.7f)
        close()
    }
}
