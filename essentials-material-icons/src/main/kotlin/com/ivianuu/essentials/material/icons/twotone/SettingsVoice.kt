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

val Icons.TwoTone.SettingsVoice: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(12.0f, 11.0f)
        curveToRelative(0.56f, 0.0f, 0.99f, -0.44f, 0.99f, -1.0f)
        lineTo(13.0f, 4.0f)
        curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
        reflectiveCurveToRelative(-1.0f, 0.45f, -1.0f, 1.0f)
        verticalLineToRelative(6.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        close()
    }
    path {
        moveTo(7.0f, 22.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        lineTo(7.0f, 24.0f)
        close()
        moveTo(12.0f, 13.0f)
        curveToRelative(1.66f, 0.0f, 2.99f, -1.34f, 2.99f, -3.0f)
        lineTo(15.0f, 4.0f)
        curveToRelative(0.0f, -1.66f, -1.34f, -3.0f, -3.0f, -3.0f)
        reflectiveCurveTo(9.0f, 2.34f, 9.0f, 4.0f)
        verticalLineToRelative(6.0f)
        curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
        close()
        moveTo(11.0f, 4.0f)
        curveToRelative(0.0f, -0.55f, 0.45f, -1.0f, 1.0f, -1.0f)
        reflectiveCurveToRelative(1.0f, 0.45f, 1.0f, 1.0f)
        verticalLineToRelative(6.0f)
        curveToRelative(0.0f, 0.56f, -0.44f, 1.0f, -1.0f, 1.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        lineTo(11.0f, 4.0f)
        close()
        moveTo(11.0f, 22.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(15.0f, 22.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(19.0f, 10.0f)
        horizontalLineToRelative(-1.7f)
        curveToRelative(0.0f, 3.0f, -2.54f, 5.1f, -5.3f, 5.1f)
        reflectiveCurveTo(6.7f, 13.0f, 6.7f, 10.0f)
        lineTo(5.0f, 10.0f)
        curveToRelative(0.0f, 3.41f, 2.72f, 6.23f, 6.0f, 6.72f)
        lineTo(11.0f, 20.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-3.28f)
        curveToRelative(3.28f, -0.49f, 6.0f, -3.31f, 6.0f, -6.72f)
        close()
    }
}
