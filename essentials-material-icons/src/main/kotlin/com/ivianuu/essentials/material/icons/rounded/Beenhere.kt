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

val Icons.Rounded.Beenhere: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(19.0f, 1.0f)
        lineTo(5.0f, 1.0f)
        curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
        lineTo(3.0f, 15.93f)
        curveToRelative(0.0f, 0.69f, 0.35f, 1.3f, 0.88f, 1.66f)
        lineToRelative(7.57f, 5.04f)
        curveToRelative(0.34f, 0.22f, 0.77f, 0.22f, 1.11f, 0.0f)
        lineToRelative(7.56f, -5.04f)
        curveToRelative(0.53f, -0.36f, 0.88f, -0.97f, 0.88f, -1.66f)
        lineTo(21.0f, 3.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(18.3f, 7.7f)
        lineToRelative(-7.59f, 7.59f)
        curveToRelative(-0.39f, 0.39f, -1.02f, 0.39f, -1.41f, 0.0f)
        lineTo(5.71f, 11.7f)
        curveToRelative(-0.39f, -0.39f, -0.39f, -1.02f, 0.0f, -1.41f)
        reflectiveCurveToRelative(1.02f, -0.39f, 1.41f, 0.0f)
        lineTo(10.0f, 13.17f)
        lineToRelative(6.88f, -6.88f)
        curveToRelative(0.39f, -0.39f, 1.02f, -0.39f, 1.41f, 0.0f)
        reflectiveCurveToRelative(0.4f, 1.02f, 0.01f, 1.41f)
        close()
    }
}
