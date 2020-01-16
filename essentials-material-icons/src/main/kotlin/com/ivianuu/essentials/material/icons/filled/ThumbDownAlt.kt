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

import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.Icons

import com.ivianuu.essentials.material.icons.lazyMaterialIcon

val Icons.Filled.ThumbDownAlt: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(22.0f, 4.0f)
        horizontalLineToRelative(-2.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(9.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(2.0f)
        verticalLineTo(4.0f)
        close()
        moveTo(2.17f, 11.12f)
        curveToRelative(-0.11f, 0.25f, -0.17f, 0.52f, -0.17f, 0.8f)
        verticalLineTo(13.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(5.5f)
        lineToRelative(-0.92f, 4.65f)
        curveToRelative(-0.05f, 0.22f, -0.02f, 0.46f, 0.08f, 0.66f)
        curveToRelative(0.23f, 0.45f, 0.52f, 0.86f, 0.88f, 1.22f)
        lineTo(10.0f, 22.0f)
        lineToRelative(6.41f, -6.41f)
        curveToRelative(0.38f, -0.38f, 0.59f, -0.89f, 0.59f, -1.42f)
        verticalLineTo(6.34f)
        curveTo(17.0f, 5.05f, 15.95f, 4.0f, 14.66f, 4.0f)
        horizontalLineToRelative(-8.1f)
        curveToRelative(-0.71f, 0.0f, -1.36f, 0.37f, -1.72f, 0.97f)
        lineToRelative(-2.67f, 6.15f)
        close()
    }
}
