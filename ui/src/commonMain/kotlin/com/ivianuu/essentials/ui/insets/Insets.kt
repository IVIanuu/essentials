/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.insets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import arrow.fx.coroutines.guarantee
import com.ivianuu.essentials.ui.AppUiDecorator
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
    var lastInsets by remember { mutableStateOf(targetInsets) }
    LaunchedEffect(animation) {
      guarantee(
        fa = { animation.animateTo(1f, animationSpec = tween(durationMillis = 150)) },
        finalizer = { lastInsets = lerp(lastInsets, targetInsets, animation.value) }
      )
    }
    remember(animation.value) { lerp(lastInsets, targetInsets, animation.value) }
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

@Immutable data class Insets(
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
  CompositionLocalProvider(
    LocalInsets provides currentInsets.copy(
      left = if (left) 0.dp else currentInsets.left,
      top = if (top) 0.dp else currentInsets.top,
      right = if (right) 0.dp else currentInsets.right,
      bottom = if (bottom) 0.dp else currentInsets.bottom
    ),
    content = content
  )
}

fun interface WindowInsetsProvider : AppUiDecorator

@Provide expect val windowInsetsProvider: WindowInsetsProvider

@Composable fun localHorizontalInsetsPadding(
  start: Dp = 0.dp,
  top: Dp = 0.dp,
  end: Dp = 0.dp,
  bottom: Dp = 0.dp
) = LocalInsets.current.toPaddingValues(start, top, end, bottom)

@Composable fun localVerticalInsetsPadding(
  start: Dp = 0.dp,
  top: Dp = 0.dp,
  end: Dp = 0.dp,
  bottom: Dp = 0.dp
) = LocalInsets.current.toPaddingValues(start, top, end, bottom)

fun Insets.toPaddingValues(
  start: Dp = 0.dp,
  top: Dp = 0.dp,
  end: Dp = 0.dp,
  bottom: Dp = 0.dp
) = PaddingValues(
  start = left + start, top = this.top + top, end = right + end, bottom = this.bottom + bottom
)
