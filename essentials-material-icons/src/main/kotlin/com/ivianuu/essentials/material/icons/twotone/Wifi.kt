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

val Icons.TwoTone.Wifi: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(1.0f, 9.0f)
        lineToRelative(2.0f, 2.0f)
        curveToRelative(4.97f, -4.97f, 13.03f, -4.97f, 18.0f, 0.0f)
        lineToRelative(2.0f, -2.0f)
        curveTo(16.93f, 2.93f, 7.08f, 2.93f, 1.0f, 9.0f)
        close()
        moveTo(9.0f, 17.0f)
        lineToRelative(3.0f, 3.0f)
        lineToRelative(3.0f, -3.0f)
        curveToRelative(-1.65f, -1.66f, -4.34f, -1.66f, -6.0f, 0.0f)
        close()
        moveTo(5.0f, 13.0f)
        lineToRelative(2.0f, 2.0f)
        curveToRelative(2.76f, -2.76f, 7.24f, -2.76f, 10.0f, 0.0f)
        lineToRelative(2.0f, -2.0f)
        curveTo(15.14f, 9.14f, 8.87f, 9.14f, 5.0f, 13.0f)
        close()
    }
}
