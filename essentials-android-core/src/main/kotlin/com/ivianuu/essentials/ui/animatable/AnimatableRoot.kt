package com.ivianuu.essentials.ui.animatable

import androidx.compose.foundation.layout.Stack
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
    children: @Composable () -> Unit
) {
    val state = remember { AnimatableRoot() }
    Providers(AnimatableRootAmbient provides state) {
        Stack(modifier = modifier.then(Modifier.animatable(Root))) {
            children()
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
