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

val Icons.TwoTone.CancelPresentation: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(3.0f, 19.1f)
        horizontalLineToRelative(18.0f)
        lineTo(21.0f, 4.95f)
        lineTo(3.0f, 4.95f)
        lineTo(3.0f, 19.1f)
        close()
        moveTo(8.0f, 9.36f)
        lineToRelative(1.41f, -1.41f)
        lineTo(12.0f, 10.54f)
        lineToRelative(2.59f, -2.59f)
        lineTo(16.0f, 9.36f)
        lineToRelative(-2.59f, 2.59f)
        lineTo(16.0f, 14.54f)
        lineToRelative(-1.41f, 1.41f)
        lineTo(12.0f, 13.36f)
        lineToRelative(-2.59f, 2.59f)
        lineTo(8.0f, 14.54f)
        lineToRelative(2.59f, -2.59f)
        lineTo(8.0f, 9.36f)
        close()
    }
    path {
        moveTo(21.0f, 3.0f)
        lineTo(3.0f, 3.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(18.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(23.0f, 5.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(21.0f, 19.0f)
        lineTo(3.0f, 19.0f)
        lineTo(3.0f, 5.0f)
        horizontalLineToRelative(18.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(9.41f, 15.95f)
        lineTo(12.0f, 13.36f)
        lineToRelative(2.59f, 2.59f)
        lineTo(16.0f, 14.54f)
        lineToRelative(-2.59f, -2.59f)
        lineTo(16.0f, 9.36f)
        lineToRelative(-1.41f, -1.41f)
        lineTo(12.0f, 10.54f)
        lineTo(9.41f, 7.95f)
        lineTo(8.0f, 9.36f)
        lineToRelative(2.59f, 2.59f)
        lineTo(8.0f, 14.54f)
        close()
    }
}
