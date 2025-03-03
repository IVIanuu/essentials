/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.material

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import essentials.ui.util.*

@Composable fun guessingContentColorFor(color: Color): Color =
  with(MaterialTheme.colorScheme) {
    when (color) {
      primary -> onPrimary
      secondary -> onSecondary
      tertiary -> onTertiary
      background -> onBackground
      surface -> onSurface
      error -> onError
      inverseSurface -> inverseOnSurface
      primaryContainer -> onPrimaryContainer
      secondaryContainer -> onSecondaryContainer
      tertiaryContainer -> onTertiaryContainer
      errorContainer -> onErrorContainer
      else -> color.contentColor
    }
  }

val Color.contentColor: Color
  get() = if (isDark) Color.White else Color.Black
