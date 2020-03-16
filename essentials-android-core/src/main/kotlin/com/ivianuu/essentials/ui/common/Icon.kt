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
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.layout.LayoutSize
import androidx.ui.material.ripple.Ripple
import androidx.ui.unit.dp

@Composable
fun IconButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = LayoutSize.Min(minSize = 40.dp),
    children: @Composable () -> Unit
) {
    Ripple(
        bounded = false,
        enabled = enabled
    ) {
        Clickable(onClick = onClick) {
            Box(modifier = modifier, gravity = Alignment.Center, children = children)
        }
    }
}
