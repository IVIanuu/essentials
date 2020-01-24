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

val Icons.Rounded.EmojiPeople: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 4.0f)
        moveToRelative(-2.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
        arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
    }
    path {
        moveTo(15.89f, 8.11f)
        curveTo(15.5f, 7.72f, 14.83f, 7.0f, 13.53f, 7.0f)
        curveToRelative(-0.21f, 0.0f, -1.42f, 0.0f, -2.54f, 0.0f)
        curveTo(8.53f, 6.99f, 6.48f, 5.2f, 6.07f, 2.85f)
        curveTo(5.99f, 2.36f, 5.58f, 2.0f, 5.09f, 2.0f)
        horizontalLineToRelative(0.0f)
        curveToRelative(-0.61f, 0.0f, -1.09f, 0.54f, -1.0f, 1.14f)
        curveTo(4.53f, 5.8f, 6.47f, 7.95f, 9.0f, 8.71f)
        verticalLineTo(21.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(0.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-5.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(5.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(0.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineTo(10.05f)
        lineToRelative(3.24f, 3.24f)
        curveToRelative(0.39f, 0.39f, 1.02f, 0.39f, 1.41f, 0.0f)
        verticalLineToRelative(0.0f)
        curveToRelative(0.39f, -0.39f, 0.39f, -1.02f, 0.0f, -1.41f)
        lineTo(15.89f, 8.11f)
        close()
    }
}
