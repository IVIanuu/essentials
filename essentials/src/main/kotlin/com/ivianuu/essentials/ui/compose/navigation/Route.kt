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
import androidx.compose.Observe
import androidx.compose.Recompose
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.OverlayEntry
import com.ivianuu.essentials.ui.compose.core.staticComposable

data class TransitionProperties(
    val direction: TransitionDirection,
    val onComplete: () -> Unit
)

enum class TransitionDirection {
    Enter, Exit
}

typealias RouteTransition = @Composable() (TransitionProperties?, @Composable() () -> Unit) -> Unit

val DefaultRouteTransition: RouteTransition = { props, children ->
    props?.onComplete?.invoke()
    children()
}

open class Route(
    opaque: Boolean = false,
    keepState: Boolean = false,
    val transition: RouteTransition = DefaultRouteTransition,
    val compose: @Composable() () -> Unit
) {

    val overlayEntry = OverlayEntry(
        opaque = opaque,
        keepState = keepState,
        compose = { compose() }
    )

    var navigator: Navigator? = null
        private set

    private var recompose: (() -> Unit)? = null
    private var state = State.Initialized
        set(value) {
            field = value
            d { "state changed $value" }
        }

    @Composable
    open fun compose() {
        staticComposable("route content") {
            Recompose {
                d { "in recompose" }
                this.recompose = it

                val children: @Composable() () -> Unit = {
                    staticComposable("content") {
                        d { "children composable" }
                        RouteAmbient.Provider(this) {
                            compose.invoke()
                        }
                    }
                }

                val onComplete = {
                    when (state) {
                        State.Pushed -> {
                            attach()
                        }
                        State.Popped -> {
                            finalize()
                        }
                    }
                }

                val props = when (state) {
                    State.Pushed -> TransitionProperties(
                        direction = TransitionDirection.Enter,
                        onComplete = onComplete
                    )
                    State.Popped -> TransitionProperties(
                        direction = TransitionDirection.Exit,
                        onComplete = onComplete
                    )
                    else -> null
                }

                Observe {
                    d { "in transition" }
                    transition(props, children)
                }
            }
        }
    }

    open fun onPush(navigator: Navigator, index: Int) {
        state = State.Pushed

        this.navigator = navigator

        if (overlayEntry in navigator.overlay.entries) {
            navigator.overlay.remove(overlayEntry)
        }

        navigator.overlay.add(index, overlayEntry)
    }

    open fun onPop() {
        state = State.Popped
        recompose!!()
    }

    private fun attach() {
        state = State.Attached
        recompose!!()
    }

    private fun finalize() {
        state = State.Destroyed
        navigator!!.overlay.remove(overlayEntry)
        this.navigator = null
        this.recompose = null
    }

    private enum class State {
        Initialized, Pushed, Attached, Popped, Destroyed
    }
}

val RouteAmbient = Ambient.of<Route>()