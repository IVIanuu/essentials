/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.layout

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

actual fun Modifier.systemBarsPadding() = this

actual fun Modifier.statusBarsPadding() = this

actual fun Modifier.navigationBarsPadding() = this

actual fun Modifier.imePadding() = this

actual val WindowInsets.Companion.systemBars
  @Composable get() = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)

actual val WindowInsets.Companion.statusBars
  @Composable get() = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)

actual val WindowInsets.Companion.navigationBars
  @Composable get() = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)

actual val WindowInsets.Companion.ime
  @Composable get() = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
