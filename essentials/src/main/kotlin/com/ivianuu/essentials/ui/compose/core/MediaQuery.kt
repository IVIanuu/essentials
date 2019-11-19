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

package com.ivianuu.essentials.ui.compose.core

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.ui.core.Density
import androidx.ui.core.Size
import androidx.ui.core.dp
import androidx.ui.layout.EdgeInsets

// todo find a better name
// todo add padding field

data class MediaQuery(
    val size: Size = Size(0.dp, 0.dp),
    val viewPadding: EdgeInsets = EdgeInsets(),
    val viewInsets: EdgeInsets,
    val density: Density = Density(0f, 0f),
    val darkMode: Boolean
) {
    val orientation: Orientation
        get() = if (size.width > size.height) Orientation.Landscape else Orientation.Portrait
}

@Composable
fun WithMediaQuery(
    children: @Composable() (MediaQuery) -> Unit
) = composable {
    children.invokeAsComposable(ambientMediaQuery())
}

fun ambientMediaQuery(): MediaQuery = effect { ambient(MediaQueryAmbient) }

@Composable
fun MediaQueryProvider(
    value: MediaQuery,
    children: @Composable() () -> Unit
) = composable {
    MediaQueryAmbient.Provider(value = value, children = children)
}

private val MediaQueryAmbient = Ambient.of<MediaQuery>()