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

package com.ivianuu.essentials.ui.core

import android.view.View
import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.AndroidComposeViewAmbient
import androidx.ui.core.Size
import androidx.ui.core.ambientDensity
import androidx.ui.core.px
import androidx.ui.core.withDensity
import com.github.ajalt.timberkt.d

val WindowSizeAmbient = Ambient.of<Size>()

@Composable
fun WindowSizeProvider(children: @Composable() () -> Unit) {
    val composeView = ambient(AndroidComposeViewAmbient)

    val density = ambientDensity()
    fun getSize() = withDensity(density) {
        Size(
            width = composeView.width.px.toDp(),
            height = composeView.height.px.toDp()
        )
    }

    val (size, setSize) = state { getSize() }
    val onLayoutChangeListener = remember {
        View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            setSize(getSize())
        } as View.OnLayoutChangeListener // todo remove type once fixed
    }

    onCommit(composeView) {
        composeView.addOnLayoutChangeListener(onLayoutChangeListener)
        onDispose { composeView.removeOnLayoutChangeListener(onLayoutChangeListener) }
    }

    d { "window size w ${size.width} h ${size.height}" }

    WindowSizeAmbient.Provider(
        value = size,
        children = children
    )
}