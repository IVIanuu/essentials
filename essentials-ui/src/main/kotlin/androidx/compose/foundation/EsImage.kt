/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package androidx.compose.foundation

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.*

@Composable inline fun Image(
  bitmap: ImageBitmap,
  modifier: Modifier = Modifier,
  alignment: Alignment = Alignment.Center,
  contentScale: ContentScale = ContentScale.Fit,
  alpha: Float = DefaultAlpha,
  colorFilter: ColorFilter? = null
) {
  Image(bitmap, null, modifier, alignment, contentScale, alpha, colorFilter)
}

@Composable inline fun Image(
  imageVector: ImageVector,
  modifier: Modifier = Modifier,
  alignment: Alignment = Alignment.Center,
  contentScale: ContentScale = ContentScale.Fit,
  alpha: Float = DefaultAlpha,
  colorFilter: ColorFilter? = null
) {
  Image(imageVector, null, modifier, alignment, contentScale, alpha, colorFilter)
}

@Composable inline fun Image(
  painter: Painter,
  modifier: Modifier = Modifier,
  alignment: Alignment = Alignment.Center,
  contentScale: ContentScale = ContentScale.Fit,
  alpha: Float = DefaultAlpha,
  colorFilter: ColorFilter? = null
) {
  Image(painter, null, modifier, alignment, contentScale, alpha, colorFilter)
}

@Composable inline fun Image(
  painterResId: Int,
  contentDescription: String? = null,
  modifier: Modifier = Modifier,
  alignment: Alignment = Alignment.Center,
  contentScale: ContentScale = ContentScale.Fit,
  alpha: Float = DefaultAlpha,
  colorFilter: ColorFilter? = null
) {
  Image(painterResource(painterResId),
    contentDescription, modifier, alignment, contentScale, alpha, colorFilter)
}