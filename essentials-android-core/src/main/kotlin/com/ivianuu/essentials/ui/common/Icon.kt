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
import androidx.ui.foundation.Clickable
import androidx.ui.layout.LayoutSize
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.material.ripple

@Composable
fun IconButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier.None,
    children: @Composable () -> Unit
) {
    Clickable(
        modifier = LayoutSize.Min(minSize = 40.dp) +
                ripple(bounded = false, enabled = enabled) + modifier,
        onClick = onClick,
        enabled = enabled,
        children = children
    )
}
