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

package com.ivianuu.essentials.ui.compose.layout

import androidx.compose.Composable
import androidx.ui.core.Dp
import androidx.ui.core.Modifier
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Spacer

@Composable
fun WidthSpacer(width: Dp) {
    Spacer(width = width)
}

@Composable
fun HeightSpacer(height: Dp) {
    Spacer(height = height)
}

@Composable
fun Spacer(size: Dp) {
    Spacer(width = size, height = size)
}

@Composable
fun Spacer(
    width: Dp? = null,
    height: Dp? = null
) {
    Spacer(modifier = (width?.let { LayoutWidth(it) }
        ?: Modifier.None) + (height?.let { LayoutHeight(it) } ?: Modifier.None))
}