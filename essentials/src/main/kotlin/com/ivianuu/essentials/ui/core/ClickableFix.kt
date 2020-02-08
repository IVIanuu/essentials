package com.ivianuu.essentials.ui.core

import androidx.compose.Composable
import androidx.ui.core.gesture.PressReleasedGestureDetector

// todo remove once fixed

@Composable
fun Clickable(
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    consumeDownOnStart: Boolean = false,
    children: @Composable() () -> Unit
) {
    PressReleasedGestureDetector(
        onRelease = onClick,
        consumeDownOnStart = consumeDownOnStart,
        children = children
    )
}