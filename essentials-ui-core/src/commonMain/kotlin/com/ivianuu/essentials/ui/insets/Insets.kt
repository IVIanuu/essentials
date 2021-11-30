/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.insets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.ivianuu.essentials.ui.UiDecorator
import com.ivianuu.essentials.ui.common.getValue
import com.ivianuu.essentials.ui.common.refOf
import com.ivianuu.essentials.ui.common.setValue
import com.ivianuu.injekt.Provide

@Composable fun InsetsPadding(
  modifier: Modifier = Modifier,
  left: Boolean = true,
  top: Boolean = true,
  right: Boolean = true,
  bottom: Boolean = true,
  animate: Boolean = true,
  content: @Composable () -> Unit
) {
  val targetInsets = LocalInsets.current

  val animatedInsets = if (!animate) targetInsets else {
    val animation = remember(targetInsets) { Animatable(0f) }
    LaunchedEffect(animation) {
      animation.animateTo(1f, animationSpec = tween(durationMillis = 150))
    }
    var lastInsets by remember { refOf(targetInsets) }
    remember(animation.value) { lerp(lastInsets, targetInsets, animation.value) }
      .also { newInsets ->
        SideEffect { lastInsets = newInsets }
      }
  }

  Box(
    modifier = Modifier.absolutePadding(
      if (left) animatedInsets.left else 0.dp,
      if (top) animatedInsets.top else 0.dp,
      if (right) animatedInsets.right else 0.dp,
      if (bottom) animatedInsets.bottom else 0.dp
    ).then(modifier)
  ) {
    ConsumeInsets(left, top, right, bottom, content)
  }
}

data class Insets(
  val left: Dp = 0.dp,
  val top: Dp = 0.dp,
  val right: Dp = 0.dp,
  val bottom: Dp = 0.dp
)

fun lerp(
  start: Insets,
  end: Insets,
  fraction: Float
) = Insets(
  left = lerp(start.left, end.left, fraction),
  top = lerp(start.top, end.top, fraction),
  right = lerp(start.right, end.right, fraction),
  bottom = lerp(start.bottom, end.bottom, fraction),
)

val LocalInsets = compositionLocalOf { Insets() }

@Composable fun ConsumeInsets(
  left: Boolean = true,
  top: Boolean = true,
  right: Boolean = true,
  bottom: Boolean = true,
  content: @Composable () -> Unit
) {
  val currentInsets = LocalInsets.current
  InsetsProvider(
    currentInsets.copy(
      left = if (left) 0.dp else currentInsets.left,
      top = if (top) 0.dp else currentInsets.top,
      right = if (right) 0.dp else currentInsets.right,
      bottom = if (bottom) 0.dp else currentInsets.bottom
    ),
    content = content
  )
}

@Composable fun InsetsProvider(
  insets: Insets,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(LocalInsets provides insets, content = content)
}

fun interface WindowInsetsProvider : UiDecorator

@Provide expect val windowInsetsProvider: WindowInsetsProvider

@Composable fun localHorizontalInsetsPadding() = LocalInsets.current.toPaddingValues()

@Composable fun localVerticalInsetsPadding() = LocalInsets.current.toPaddingValues()

fun Insets.toPaddingValues() = PaddingValues(
  start = left, top = top, end = right, bottom = bottom
)
