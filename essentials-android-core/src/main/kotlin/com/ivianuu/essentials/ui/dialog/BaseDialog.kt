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

package com.ivianuu.essentials.ui.dialog

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.layout.padding
import androidx.ui.layout.preferredWidthIn
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.unit.dp

@Composable
fun BaseDialog(
    modifier: Modifier = Modifier,
    children: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(all = 32.dp)
            .preferredWidthIn(minWidth = 280.dp, maxWidth = 356.dp)
            .plus(modifier),
        color = MaterialTheme.colors.surface,
        elevation = 24.dp,
        shape = MaterialTheme.shapes.medium,
        content = children
    )
}
