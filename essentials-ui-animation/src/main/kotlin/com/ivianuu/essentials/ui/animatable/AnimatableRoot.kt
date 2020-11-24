/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.ui.animatable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.Modifier

@Composable
fun ProvideAnimatableRoot(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = remember { AnimatableRoot() }
    Providers(AnimatableRootAmbient provides state) {
        Box(modifier = modifier.then(Modifier.animatable(Root))) {
            content()
            state.animationOverlayEntries.forEach { overlay ->
                key(overlay) {
                    overlay.content()
                }
            }
        }
    }
}

@Composable
fun animatableFor(tag: Any): Animatable = AnimatableRootAmbient.current.animatableFor(tag)

@Stable
class AnimatableRoot {

    internal val animationOverlayEntries = mutableStateListOf<AnimationOverlayEntry>()

    private val animatables = mutableMapOf<Any, AnimatableState>()

    @Composable
    fun animatableFor(tag: Any): Animatable {
        val state = animatables.getOrPut(tag) { AnimatableState(tag) }
        state.refCount()
        return state.animatable
    }

    @Stable
    private inner class AnimatableState(private val tag: Any) {

        val animatable by mutableStateOf(Animatable(tag))

        private var refCount = 0

        @Composable
        fun refCount() {
            onActive {
                refCount++
                onDispose {
                    refCount--
                    if (refCount == 0) {
                        animatables -= tag
                    }
                }
            }
        }
    }
}

@Stable
internal class AnimationOverlayEntry(
    content: @Composable () -> Unit
) {
    var content by mutableStateOf(content)
}

@Composable
fun animationOverlay(overlayContent: @Composable () -> Unit) {
    val entry = remember { AnimationOverlayEntry(overlayContent) }
    entry.content = overlayContent
    val root = AnimatableRootAmbient.current
    onActive {
        root.animationOverlayEntries += entry
        onDispose { root.animationOverlayEntries -= entry }
    }
}

val Root = Any()

val AnimatableRootAmbient = staticAmbientOf<AnimatableRoot> {
    error("No AnimatableRoot found")
}
