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

package com.ivianuu.essentials.ui.compose.navigation

import androidx.compose.Ambient
import androidx.compose.Composable
import com.ivianuu.essentials.ui.compose.common.OverlayEntry

open class Route(
    opaque: Boolean = false,
    keepState: Boolean = false,
    val compose: @Composable() () -> Unit
) {

    val overlayEntry = OverlayEntry(
        opaque = opaque,
        keepState = keepState,
        compose = { compose() }
    )

    var navigator: Navigator? = null

    @Composable
    open fun compose() {
        RouteAmbient.Provider(this) {
            compose.invoke()
        }
    }

    open fun onPush(navigator: Navigator, index: Int) {
        this.navigator = navigator

        if (overlayEntry in navigator.overlay.entries) {
            navigator.overlay.remove(overlayEntry)
        }

        navigator.overlay.add(index, overlayEntry)
    }

    open fun onPop() {
        navigator!!.overlay.remove(overlayEntry)
        this.navigator = null
    }

}

val RouteAmbient = Ambient.of<Route>()