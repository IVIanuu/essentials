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
import androidx.compose.ambient
import androidx.ui.core.Density
import androidx.ui.core.Size
import androidx.ui.core.dp
import androidx.ui.layout.EdgeInsets
import com.ivianuu.essentials.ui.compose.common.UpdateProvider
import com.ivianuu.essentials.ui.compose.common.Updateable
import com.ivianuu.essentials.ui.compose.common.framed

// todo find a better name
// todo add padding field

interface MediaQuery : Updateable<MediaQuery> {
    val size: Size
    val viewPadding: EdgeInsets
    val viewInsets: EdgeInsets
    val density: Density
    val darkMode: Boolean

    val orientation: Orientation
        get() = if (size.width > size.height) Orientation.Landscape else Orientation.Portrait

    fun copy(
        size: Size = this.size,
        viewPadding: EdgeInsets = this.viewPadding,
        viewInsets: EdgeInsets = this.viewInsets,
        density: Density = this.density,
        darkMode: Boolean = this.darkMode
    ): MediaQuery
}

fun MediaQuery(
    size: Size = Size(0.dp, 0.dp),
    viewPadding: EdgeInsets = EdgeInsets(),
    viewInsets: EdgeInsets,
    density: Density = Density(0f, 0f),
    darkMode: Boolean
): MediaQuery {
    return ObservableMediaQuery(
        size = size,
        viewPadding = viewPadding,
        viewInsets = viewInsets,
        density = density,
        darkMode = darkMode
    )
}

private class ObservableMediaQuery(
    size: Size,
    viewPadding: EdgeInsets,
    viewInsets: EdgeInsets,
    density: Density,
    darkMode: Boolean
) : MediaQuery {

    constructor(
        other: MediaQuery
    ) : this(
        size = other.size,
        viewPadding = other.viewPadding,
        viewInsets = other.viewInsets,
        density = other.density,
        darkMode = other.darkMode
    )

    override var size by framed(size, true)
    override var viewPadding by framed(viewPadding, true)
    override var viewInsets by framed(viewInsets, true)
    override var density by framed(density, true)
    override var darkMode by framed(darkMode, true)

    override fun copy(
        size: Size,
        viewPadding: EdgeInsets,
        viewInsets: EdgeInsets,
        density: Density,
        darkMode: Boolean
    ): MediaQuery = ObservableMediaQuery(
        size = size,
        viewPadding = viewPadding,
        viewInsets = viewInsets,
        density = density,
        darkMode = darkMode
    )

    override fun updateFrom(other: MediaQuery) {
        size = other.size
        viewPadding = other.viewPadding
        viewInsets = other.viewInsets
        density = other.density
        darkMode = other.darkMode
    }
}

@Composable
fun WithMediaQuery(
    children: @Composable() (MediaQuery) -> Unit
) {
    children(ambientMediaQuery())
}

@Composable
fun ambientMediaQuery(): MediaQuery = ambient(MediaQueryAmbient)

@Composable
fun MediaQueryProvider(
    value: MediaQuery,
    children: @Composable() () -> Unit
) {
    MediaQueryAmbient.UpdateProvider(
        value = value,
        children = children
    )
}

private val MediaQueryAmbient = Ambient.of<MediaQuery>()
