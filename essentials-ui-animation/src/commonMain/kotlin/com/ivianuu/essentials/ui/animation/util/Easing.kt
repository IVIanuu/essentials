/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.util

fun interval(
  start: Float,
  end: Float,
  fraction: Float
): Float = ((fraction - start) / (end - start)).coerceIn(0f, 1f)
