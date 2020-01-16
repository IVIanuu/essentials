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

val Icons.Rounded.BarChart: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(6.4f, 9.2f)
        horizontalLineToRelative(0.2f)
        curveToRelative(0.77f, 0.0f, 1.4f, 0.63f, 1.4f, 1.4f)
        verticalLineToRelative(7.0f)
        curveToRelative(0.0f, 0.77f, -0.63f, 1.4f, -1.4f, 1.4f)
        horizontalLineToRelative(-0.2f)
        curveToRelative(-0.77f, 0.0f, -1.4f, -0.63f, -1.4f, -1.4f)
        verticalLineToRelative(-7.0f)
        curveToRelative(0.0f, -0.77f, 0.63f, -1.4f, 1.4f, -1.4f)
        close()
        moveTo(12.0f, 5.0f)
        curveToRelative(0.77f, 0.0f, 1.4f, 0.63f, 1.4f, 1.4f)
        verticalLineToRelative(11.2f)
        curveToRelative(0.0f, 0.77f, -0.63f, 1.4f, -1.4f, 1.4f)
        curveToRelative(-0.77f, 0.0f, -1.4f, -0.63f, -1.4f, -1.4f)
        lineTo(10.6f, 6.4f)
        curveToRelative(0.0f, -0.77f, 0.63f, -1.4f, 1.4f, -1.4f)
        close()
        moveTo(17.6f, 13.0f)
        curveToRelative(0.77f, 0.0f, 1.4f, 0.63f, 1.4f, 1.4f)
        verticalLineToRelative(3.2f)
        curveToRelative(0.0f, 0.77f, -0.63f, 1.4f, -1.4f, 1.4f)
        curveToRelative(-0.77f, 0.0f, -1.4f, -0.63f, -1.4f, -1.4f)
        verticalLineToRelative(-3.2f)
        curveToRelative(0.0f, -0.77f, 0.63f, -1.4f, 1.4f, -1.4f)
        close()
    }
}
