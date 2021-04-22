package com.ivianuu.essentials.ui.animation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Composable
fun AnimationRootProvider(
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
    AnimationRootProvider(content = content)
}

@Given
val animationRootProviderConfig = UiDecoratorConfig<AnimationRootProvider>(
    dependencies = setOf(typeKeyOf<AppTheme>())
)
