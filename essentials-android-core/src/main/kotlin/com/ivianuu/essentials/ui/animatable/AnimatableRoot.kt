package com.ivianuu.essentials.ui.animatable

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.frames.modelListOf
import androidx.compose.mutableStateOf
import androidx.compose.onPreCommit
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import androidx.ui.core.Modifier
import com.ivianuu.essentials.ui.animatedstack.StatefulStack
import com.ivianuu.essentials.ui.animatedstack.StatefulStackEntry

@Composable
fun AnimatableRoot(
    modifier: Modifier = Modifier,
    children: @Composable () -> Unit
) {
    val state = remember { AnimatableRoot() }
    Providers(AnimatableRootAmbient provides state) {
        val contentStackEntry = remember { StatefulStackEntry(content = children) }
        contentStackEntry.content = children
        val allEntries = listOf(contentStackEntry) + state.animationOverlayEntries
        StatefulStack(
            modifier = modifier + Modifier.animatable(Root),
            entries = allEntries
        )
    }
}

@Composable
fun animatableFor(tag: Any): Animatable = AnimatableRootAmbient.current.animatableFor(tag)

class AnimatableRoot {

    internal val animationOverlayEntries = modelListOf<StatefulStackEntry>()

    private val animatables = mutableMapOf<Any, AnimatableState>()

    @Composable
    fun animatableFor(tag: Any): Animatable {
        val state = animatables.getOrPut(tag) { AnimatableState(tag) }
        state.refCount()
        return state.state.value
    }

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

@Composable
fun animationOverlay(overlayContent: @Composable () -> Unit) {
    val stackEntry = remember {
        StatefulStackEntry(opaque = true, content = overlayContent)
    }
    stackEntry.content = overlayContent
    val root = AnimatableRootAmbient.current
    onPreCommit(true) {
        root.animationOverlayEntries += stackEntry
        onDispose { root.animationOverlayEntries -= stackEntry }
    }
}

val Root = Any()

val AnimatableRootAmbient = staticAmbientOf<AnimatableRoot> {
    error("No AnimatableRoot found")
}
