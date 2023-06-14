/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package androidx.compose.material3

import androidx.compose.material.LocalContentAlpha
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

@Composable inline fun Icon(
  imageVector: ImageVector,
  modifier: Modifier = Modifier,
  tint: Color = androidx.compose.material.LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
  androidx.compose.material.Icon(imageVector, null, modifier, tint)
}

@Composable inline fun Icon(
  bitmap: ImageBitmap,
  modifier: Modifier = Modifier,
  tint: Color = androidx.compose.material.LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
  androidx.compose.material.Icon(bitmap, null, modifier, tint)
}

@Composable inline fun Icon(
  painter: Painter,
  modifier: Modifier = Modifier,
  tint: Color = androidx.compose.material.LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
  androidx.compose.material.Icon(painter, null, modifier, tint)
}

@Composable inline fun Icon(
  painterResId: Int,
  modifier: Modifier = Modifier,
  tint: Color = androidx.compose.material.LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
  androidx.compose.material.Icon(painterResource(painterResId), null, modifier, tint)
}
