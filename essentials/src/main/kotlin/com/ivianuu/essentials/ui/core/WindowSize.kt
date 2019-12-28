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
import androidx.compose.Stable
import androidx.compose.ambient
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.AndroidComposeViewAmbient
import androidx.ui.core.Px
import androidx.ui.core.px
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.common.UpdateProvider
import com.ivianuu.essentials.ui.common.Updateable
import com.ivianuu.essentials.ui.common.framed

val WindowSizeAmbient = Ambient.of<WindowSize>()

@Stable
class WindowSize(width: Px, height: Px) : Updateable<WindowSize> {

    var width by framed(width)
        private set
    var height by framed(height)
        private set

    override fun updateFrom(other: WindowSize) {
        width = other.width
        height = other.height
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WindowSize

        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width.hashCode()
        result = 31 * result + height.hashCode()
        return result
    }

    override fun toString(): String {
        return "WindowSize(w=${width}, h=${height})"
    }

}

@Composable
fun WindowSizeProvider(children: @Composable() () -> Unit) {
    val composeView = ambient(AndroidComposeViewAmbient)

    val (windowSize, setWindowSize) = state {
        WindowSize(
            width = composeView.width.px,
            height = composeView.height.px
        )
    }
    val onLayoutChangeListener = remember {
        View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            setWindowSize(
                WindowSize(
                    width = composeView.width.px,
                    height = composeView.height.px
                )
            )
        } as View.OnLayoutChangeListener // todo remove type once fixed
    }

    onCommit(composeView) {
        composeView.addOnLayoutChangeListener(onLayoutChangeListener)
        onDispose { composeView.removeOnLayoutChangeListener(onLayoutChangeListener) }
    }

    remember(windowSize) {
        d { "window size $windowSize" }
    }

    WindowSizeAmbient.UpdateProvider(
        value = windowSize,
        children = children
    )
}
