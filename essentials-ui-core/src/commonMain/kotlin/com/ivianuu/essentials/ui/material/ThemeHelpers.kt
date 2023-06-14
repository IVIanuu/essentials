/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import com.ivianuu.essentials.ui.util.isDark

@Composable fun guessingContentColorFor(color: Color): Color =
  contentColorFor(color).takeOrElse { if (color.isDark) Color.White else Color.Black }
