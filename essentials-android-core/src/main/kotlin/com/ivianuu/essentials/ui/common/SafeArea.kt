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
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.layout.InnerPadding
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import androidx.ui.unit.max
import com.ivianuu.essentials.ui.core.WindowInsets
import com.ivianuu.essentials.ui.core.WindowInsetsProvider
import com.ivianuu.essentials.ui.core.ambientWindowInsets

// todo ideally this should be a modifier

@Composable
fun SafeArea(
    modifier: Modifier = Modifier,
    left: Boolean = true,
    top: Boolean = true,
    right: Boolean = true,
    bottom: Boolean = true,
    children: @Composable () -> Unit
) {
    fun safeAreaValue(
        enabled: Boolean,
        insetsValue: Dp,
        paddingValue: Dp
    ): Dp {
        if (!enabled) return 0.dp
        return max(insetsValue, paddingValue)
    }

    val windowInsets = ambientWindowInsets()

    WindowInsetsProvider(
        value = WindowInsets(
            viewPadding = InnerPadding(
                start = if (left) 0.dp else windowInsets.viewPadding.start,
                top = if (top) 0.dp else windowInsets.viewPadding.top,
                end = if (right) 0.dp else windowInsets.viewPadding.end,
                bottom = if (bottom) 0.dp else windowInsets.viewPadding.bottom
            ),
            viewInsets = windowInsets.viewInsets
        )
    ) {
        Box(
            modifier = modifier,
            paddingStart = safeAreaValue(
                left,
                windowInsets.viewInsets.start,
                windowInsets.viewPadding.start
            ),
            paddingTop = safeAreaValue(
                top,
                windowInsets.viewInsets.top,
                windowInsets.viewPadding.top
            ),
            paddingEnd = safeAreaValue(
                right,
                windowInsets.viewInsets.end,
                windowInsets.viewPadding.end
            ),
            paddingBottom = safeAreaValue(
                bottom,
                windowInsets.viewInsets.bottom,
                windowInsets.viewPadding.bottom
            ),
            children = children
        )
    }
}
