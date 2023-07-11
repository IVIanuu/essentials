/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.layout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

fun Modifier.center() = align(align = Alignment.Center)

fun Modifier.align(align: Alignment) = fillMaxSize()
  .wrapContentSize(align = align)
