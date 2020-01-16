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

val Icons.TwoTone.RateReview: VectorAsset by lazyMaterialIcon {
    path(fillAlpha = 0.3f) {
        moveTo(4.0f, 17.17f)
        lineToRelative(0.59f, -0.59f)
        lineToRelative(0.58f, -0.58f)
        horizontalLineTo(20.0f)
        verticalLineTo(4.0f)
        horizontalLineTo(4.0f)
        verticalLineToRelative(13.17f)
        close()
        moveTo(18.0f, 14.0f)
        horizontalLineToRelative(-7.5f)
        lineToRelative(2.0f, -2.0f)
        horizontalLineTo(18.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(6.0f, 11.53f)
        lineToRelative(5.88f, -5.88f)
        curveToRelative(0.2f, -0.2f, 0.51f, -0.2f, 0.71f, 0.0f)
        lineToRelative(1.77f, 1.77f)
        curveToRelative(0.2f, 0.2f, 0.2f, 0.51f, 0.0f, 0.71f)
        lineTo(8.47f, 14.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(-2.47f)
        close()
    }
    path {
        moveTo(20.0f, 2.0f)
        lineTo(4.0f, 2.0f)
        curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
        lineTo(2.0f, 22.0f)
        lineToRelative(4.0f, -4.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        lineTo(22.0f, 4.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(20.0f, 16.0f)
        lineTo(5.17f, 16.0f)
        lineToRelative(-0.59f, 0.59f)
        lineToRelative(-0.58f, 0.58f)
        lineTo(4.0f, 4.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(12.0f)
        close()
        moveTo(10.5f, 14.0f)
        lineTo(18.0f, 14.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(-5.5f)
        close()
        moveTo(14.36f, 8.13f)
        curveToRelative(0.2f, -0.2f, 0.2f, -0.51f, 0.0f, -0.71f)
        lineToRelative(-1.77f, -1.77f)
        curveToRelative(-0.2f, -0.2f, -0.51f, -0.2f, -0.71f, 0.0f)
        lineTo(6.0f, 11.53f)
        lineTo(6.0f, 14.0f)
        horizontalLineToRelative(2.47f)
        lineToRelative(5.89f, -5.87f)
        close()
    }
}
