/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.core.max
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.Padding
import com.ivianuu.essentials.ui.core.WithWindowInsets

@Composable
fun SafeArea(
    left: Boolean = true,
    top: Boolean = true,
    right: Boolean = true,
    bottom: Boolean = true,
    minimum: EdgeInsets? = null,
    children: @Composable() () -> Unit
) {
    WithWindowInsets { windowInsets ->
        fun safeAreaValue(
            enabled: Boolean,
            insetsValue: Dp,
            paddingValue: Dp,
            min: Dp?
        ): Dp {
            if (!enabled) return 0.dp
            return max(max(insetsValue, paddingValue), min ?: 0.dp)
        }

        val leftPadding = safeAreaValue(
            left,
            windowInsets.viewInsets.left,
            windowInsets.viewPadding.left,
            minimum?.left
        )
        val topPadding =
            safeAreaValue(
                top,
                windowInsets.viewInsets.top,
                windowInsets.viewPadding.top,
                minimum?.top
            )
        val rightPadding = safeAreaValue(
            right,
            windowInsets.viewInsets.right,
            windowInsets.viewPadding.right,
            minimum?.right
        )
        val bottomPadding = safeAreaValue(
            bottom,
            windowInsets.viewInsets.bottom,
            windowInsets.viewPadding.bottom,
            minimum?.bottom
        )
        Padding(
            left = leftPadding,
            top = topPadding,
            right = rightPadding,
            bottom = bottomPadding,
            children = children
        )
    }
}
