package com.ivianuu.essentials.ui.animation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*

@Composable
fun ProvideAnimationRoot(
    modifier: Modifier = Modifier,
    animationRoot: AnimationRoot = remember { AnimationRoot() },
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalAnimationRoot provides animationRoot) {
        Box(modifier) {
            content()
            animationRoot.animationOverlays.toList().forEach { overlay ->
                key(overlay) { overlay() }
            }
        }
    }
}

val LocalAnimationRoot = staticCompositionLocalOf<AnimationRoot> {
    error("AnimationRoot not provided")
}

@Stable
class AnimationRoot {
    internal val animationOverlays = mutableStateListOf<@Composable () -> Unit>()
}

typealias AnimationRootProvider = UiDecorator

@Given
val animationRootProvider: AnimationRootProvider = { content ->
    ProvideAnimationRoot(content = content)
}
