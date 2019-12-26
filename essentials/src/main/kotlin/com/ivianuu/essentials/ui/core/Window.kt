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

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.frames.modelListOf
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.ui.graphics.Color
import com.ivianuu.essentials.ui.material.Surface

@Composable
fun Window(children: @Composable() () -> Unit) {
    val state = remember { WindowColorState() }
    Surface(color = state.windowColors.lastOrNull() ?: Color.Black) {
        WindowColorStateAmbient.Provider(value = state, children = children)
    }
}

private class WindowColorState {

    val windowColors = modelListOf<Color>()

    fun registerWindowColor(color: Color) {
        windowColors += color
    }

    fun unregisterWindowColor(color: Color) {
        windowColors.removeAt(windowColors.lastIndexOf(color))
    }
}

private val WindowColorStateAmbient = Ambient.of<WindowColorState>()

private val WindowColorAmbient = Ambient.of { Color.White }

@Composable
fun ProvideWindowColor(color: Color, children: @Composable() () -> Unit) {
    val state = ambient(WindowColorStateAmbient)
    onCommit(color) {
        state.registerWindowColor(color)
        onDispose { state.unregisterWindowColor(color) }
    }

    WindowColorAmbient.Provider(value = color, children = children)
}

@Composable
fun ambientWindowColor(): Color = ambient(WindowColorAmbient)