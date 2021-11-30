/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package androidx.compose.foundation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

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