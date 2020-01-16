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

import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group

val Icons.TwoTone.Accessible: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 4.0f)
        moveToRelative(-2.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
    }
    path {
        moveTo(19.0f, 13.0f)
        verticalLineToRelative(-2.0f)
        curveToRelative(-1.54f, 0.02f, -3.09f, -0.75f, -4.07f, -1.83f)
        lineToRelative(-1.29f, -1.43f)
        curveToRelative(-0.17f, -0.19f, -0.38f, -0.34f, -0.61f, -0.45f)
        curveToRelative(-0.01f, 0.0f, -0.01f, -0.01f, -0.02f, -0.01f)
        lineTo(13.0f, 7.28f)
        curveToRelative(-0.35f, -0.2f, -0.75f, -0.3f, -1.19f, -0.26f)
        curveTo(10.76f, 7.11f, 10.0f, 8.04f, 10.0f, 9.09f)
        lineTo(10.0f, 15.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(5.0f)
        verticalLineToRelative(5.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-5.5f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        horizontalLineToRelative(-3.0f)
        verticalLineToRelative(-3.45f)
        curveToRelative(1.29f, 1.07f, 3.25f, 1.94f, 5.0f, 1.95f)
        close()
        moveTo(12.83f, 18.0f)
        curveToRelative(-0.41f, 1.16f, -1.52f, 2.0f, -2.83f, 2.0f)
        curveToRelative(-1.66f, 0.0f, -3.0f, -1.34f, -3.0f, -3.0f)
        curveToRelative(0.0f, -1.31f, 0.84f, -2.41f, 2.0f, -2.83f)
        lineTo(9.0f, 12.1f)
        curveToRelative(-2.28f, 0.46f, -4.0f, 2.48f, -4.0f, 4.9f)
        curveToRelative(0.0f, 2.76f, 2.24f, 5.0f, 5.0f, 5.0f)
        curveToRelative(2.42f, 0.0f, 4.44f, -1.72f, 4.9f, -4.0f)
        horizontalLineToRelative(-2.07f)
        close()
    }
}
