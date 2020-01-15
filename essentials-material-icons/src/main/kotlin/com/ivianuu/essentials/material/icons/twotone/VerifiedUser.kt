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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.VerifiedUser: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 1.0f)
        lineTo(3.0f, 5.0f)
        verticalLineToRelative(6.0f)
        curveToRelative(0.0f, 5.55f, 3.84f, 10.74f, 9.0f, 12.0f)
        curveToRelative(5.16f, -1.26f, 9.0f, -6.45f, 9.0f, -12.0f)
        lineTo(21.0f, 5.0f)
        lineToRelative(-9.0f, -4.0f)
        close()
        moveTo(19.0f, 11.0f)
        curveToRelative(0.0f, 4.52f, -2.98f, 8.69f, -7.0f, 9.93f)
        curveToRelative(-4.02f, -1.24f, -7.0f, -5.41f, -7.0f, -9.93f)
        lineTo(5.0f, 6.3f)
        lineToRelative(7.0f, -3.11f)
        lineToRelative(7.0f, 3.11f)
        lineTo(19.0f, 11.0f)
        close()
        moveTo(7.41f, 11.59f)
        lineTo(6.0f, 13.0f)
        lineToRelative(4.0f, 4.0f)
        lineToRelative(8.0f, -8.0f)
        lineToRelative(-1.41f, -1.42f)
        lineTo(10.0f, 14.17f)
        close()
    }
    path(fillAlpha = 0.3f) {
        moveTo(5.0f, 6.3f)
        verticalLineTo(11.0f)
        curveToRelative(0.0f, 4.52f, 2.98f, 8.69f, 7.0f, 9.93f)
        curveToRelative(4.02f, -1.23f, 7.0f, -5.41f, 7.0f, -9.93f)
        verticalLineTo(6.3f)
        lineToRelative(-7.0f, -3.11f)
        lineTo(5.0f, 6.3f)
        close()
        moveTo(18.0f, 9.0f)
        lineToRelative(-8.0f, 8.0f)
        lineToRelative(-4.0f, -4.0f)
        lineToRelative(1.41f, -1.41f)
        lineTo(10.0f, 14.17f)
        lineToRelative(6.59f, -6.59f)
        lineTo(18.0f, 9.0f)
        close()
    }
}
