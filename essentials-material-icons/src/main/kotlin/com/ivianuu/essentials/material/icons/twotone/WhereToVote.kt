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

val Icons.TwoTone.WhereToVote: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 3.0f)
        curveTo(8.69f, 3.0f, 6.0f, 5.69f, 6.0f, 9.0f)
        curveToRelative(0.0f, 3.54f, 3.82f, 8.86f, 6.0f, 11.47f)
        curveToRelative(1.75f, -2.11f, 6.0f, -7.63f, 6.0f, -11.47f)
        curveToRelative(0.0f, -3.31f, -2.69f, -6.0f, -6.0f, -6.0f)
        close()
        moveTo(10.47f, 14.0f)
        lineToRelative(-3.18f, -3.18f)
        lineTo(8.71f, 9.4f)
        lineToRelative(1.77f, 1.77f)
        lineToRelative(4.6f, -4.6f)
        lineToRelative(1.41f, 1.41f)
        lineTo(10.47f, 14.0f)
        close()
    }
    path {
        moveTo(12.0f, 1.0f)
        curveTo(7.59f, 1.0f, 4.0f, 4.59f, 4.0f, 9.0f)
        curveToRelative(0.0f, 5.57f, 6.96f, 13.34f, 7.26f, 13.67f)
        lineToRelative(0.74f, 0.82f)
        lineToRelative(0.74f, -0.82f)
        curveTo(13.04f, 22.34f, 20.0f, 14.57f, 20.0f, 9.0f)
        curveToRelative(0.0f, -4.41f, -3.59f, -8.0f, -8.0f, -8.0f)
        close()
        moveTo(12.0f, 20.47f)
        curveTo(9.82f, 17.86f, 6.0f, 12.54f, 6.0f, 9.0f)
        curveToRelative(0.0f, -3.31f, 2.69f, -6.0f, 6.0f, -6.0f)
        reflectiveCurveToRelative(6.0f, 2.69f, 6.0f, 6.0f)
        curveToRelative(0.0f, 3.83f, -4.25f, 9.36f, -6.0f, 11.47f)
        close()
        moveTo(15.07f, 6.57f)
        lineToRelative(-4.6f, 4.6f)
        lineTo(8.71f, 9.4f)
        lineToRelative(-1.42f, 1.42f)
        lineTo(10.47f, 14.0f)
        lineToRelative(6.01f, -6.01f)
        close()
    }
}
