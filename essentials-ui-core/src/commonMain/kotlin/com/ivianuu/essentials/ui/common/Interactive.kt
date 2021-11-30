/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

fun Modifier.interactive(interactive: Boolean): Modifier =
  alpha(alpha = if (interactive) 1f else 0.5f)
    .absorbPointer(enabled = !interactive)
