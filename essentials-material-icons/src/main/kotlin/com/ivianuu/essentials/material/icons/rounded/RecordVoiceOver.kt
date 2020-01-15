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

import com.ivianuu.essentials.ui.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.Rounded.RecordVoiceOver: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(9.0f, 9.0f)
        moveToRelative(-4.0f, 0.0f)
        arcToRelative(4.0f, 4.0f, 0.0f, true, true, 8.0f, 0.0f)
        arcToRelative(4.0f, 4.0f, 0.0f, true, true, -8.0f, 0.0f)
    }
    path {
        moveTo(9.0f, 15.0f)
        curveToRelative(-2.67f, 0.0f, -8.0f, 1.34f, -8.0f, 4.0f)
        verticalLineToRelative(1.0f)
        curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
        verticalLineToRelative(-1.0f)
        curveToRelative(0.0f, -2.66f, -5.33f, -4.0f, -8.0f, -4.0f)
        close()
        moveTo(15.47f, 7.77f)
        curveToRelative(0.32f, 0.79f, 0.32f, 1.67f, 0.0f, 2.46f)
        curveToRelative(-0.19f, 0.47f, -0.11f, 1.0f, 0.25f, 1.36f)
        lineToRelative(0.03f, 0.03f)
        curveToRelative(0.58f, 0.58f, 1.57f, 0.46f, 1.95f, -0.27f)
        curveToRelative(0.76f, -1.45f, 0.76f, -3.15f, -0.02f, -4.66f)
        curveToRelative(-0.38f, -0.74f, -1.38f, -0.88f, -1.97f, -0.29f)
        lineToRelative(-0.01f, 0.01f)
        curveToRelative(-0.34f, 0.35f, -0.42f, 0.89f, -0.23f, 1.36f)
        close()
        moveTo(19.18f, 2.89f)
        curveToRelative(-0.4f, 0.4f, -0.46f, 1.02f, -0.13f, 1.48f)
        curveToRelative(1.97f, 2.74f, 1.96f, 6.41f, -0.03f, 9.25f)
        curveToRelative(-0.32f, 0.45f, -0.25f, 1.07f, 0.14f, 1.46f)
        lineToRelative(0.03f, 0.03f)
        curveToRelative(0.49f, 0.49f, 1.32f, 0.45f, 1.74f, -0.1f)
        curveToRelative(2.75f, -3.54f, 2.76f, -8.37f, 0.0f, -12.02f)
        curveToRelative(-0.42f, -0.55f, -1.26f, -0.59f, -1.75f, -0.1f)
        close()
    }
}
