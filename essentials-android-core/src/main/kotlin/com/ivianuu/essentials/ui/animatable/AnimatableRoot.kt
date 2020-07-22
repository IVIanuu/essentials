package com.ivianuu.essentials.ui.animatable

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.Stable
import androidx.compose.getValue
import androidx.compose.key
import androidx.compose.mutableStateListOf
import androidx.compose.mutableStateOf
import androidx.compose.onPreCommit
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import androidx.ui.core.Modifier
import androidx.ui.layout.Stack

@Composable
fun ProvideAnimatableRoot(
    modifier: Modifier = Modifier,
    children: @Composable () -> Unit
) {
    val state = remember { AnimatableRoot() }
    Providers(AnimatableRootAmbient provides state) {
        Stack(modifier = modifier + Modifier.animatable(Root)) {
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
        return state.state.value
    }

    @Stable
    private inner class AnimatableState(private val tag: Any) {

        val state = mutableStateOf(Animatable(tag))

        private var refCount = 0

        @Composable
        fun refCount() {
            onPreCommit(true) {
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
    onPreCommit(true) {
        root.animationOverlayEntries += entry
        onDispose { root.animationOverlayEntries -= entry }
    }
}

val Root = Any()

val AnimatableRootAmbient = staticAmbientOf<AnimatableRoot> {
    error("No AnimatableRoot found")
}
