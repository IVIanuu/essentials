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

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path

val Icons.TwoTone.UnfoldLess: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(7.41f, 18.59f)
        lineTo(8.83f, 20.0f)
        lineTo(12.0f, 16.83f)
        lineTo(15.17f, 20.0f)
        lineToRelative(1.41f, -1.41f)
        lineTo(12.0f, 14.0f)
        lineToRelative(-4.59f, 4.59f)
        close()
        moveTo(16.59f, 5.41f)
        lineTo(15.17f, 4.0f)
        lineTo(12.0f, 7.17f)
        lineTo(8.83f, 4.0f)
        lineTo(7.41f, 5.41f)
        lineTo(12.0f, 10.0f)
        lineToRelative(4.59f, -4.59f)
        close()
    }
}
