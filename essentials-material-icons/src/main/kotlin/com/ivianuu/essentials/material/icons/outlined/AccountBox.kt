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

package com.ivianuu.essentials.material.icons.outlined

import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.composehelpers.vector.VectorAsset
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.material.icons.group

val Icons.Outlined.AccountBox: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(19.0f, 5.0f)
        verticalLineToRelative(14.0f)
        lineTo(5.0f, 19.0f)
        lineTo(5.0f, 5.0f)
        horizontalLineToRelative(14.0f)
        moveToRelative(0.0f, -2.0f)
        lineTo(5.0f, 3.0f)
        curveToRelative(-1.11f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(21.0f, 5.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(12.0f, 14.0f)
        curveToRelative(-1.65f, 0.0f, -3.0f, -1.35f, -3.0f, -3.0f)
        reflectiveCurveToRelative(1.35f, -3.0f, 3.0f, -3.0f)
        reflectiveCurveToRelative(3.0f, 1.35f, 3.0f, 3.0f)
        reflectiveCurveToRelative(-1.35f, 3.0f, -3.0f, 3.0f)
        close()
        moveTo(12.0f, 10.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
        reflectiveCurveToRelative(0.45f, 1.0f, 1.0f, 1.0f)
        reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
        reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
        close()
        moveTo(18.0f, 20.0f)
        lineTo(6.0f, 20.0f)
        verticalLineToRelative(-1.53f)
        curveToRelative(0.0f, -2.5f, 3.97f, -3.58f, 6.0f, -3.58f)
        reflectiveCurveToRelative(6.0f, 1.08f, 6.0f, 3.58f)
        lineTo(18.0f, 18.0f)
        close()
        moveTo(8.31f, 18.0f)
        horizontalLineToRelative(7.38f)
        curveToRelative(-0.69f, -0.56f, -2.38f, -1.12f, -3.69f, -1.12f)
        reflectiveCurveToRelative(-3.01f, 0.56f, -3.69f, 1.12f)
        close()
    }
}
