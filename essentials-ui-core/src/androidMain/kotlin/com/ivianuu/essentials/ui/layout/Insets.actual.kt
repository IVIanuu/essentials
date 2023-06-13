/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.layout

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual fun Modifier.systemBarsPadding() = systemBarsPadding()

actual fun Modifier.statusBarsPadding() = statusBarsPadding()

actual fun Modifier.navigationBarsPadding() = navigationBarsPadding()

actual fun Modifier.imePadding() = imePadding()

actual val WindowInsets.Companion.systemBars
  @Composable get() = systemBars

actual val WindowInsets.Companion.statusBars
  @Composable get() = statusBars

actual val WindowInsets.Companion.navigationBars
  @Composable get() = navigationBars

actual val WindowInsets.Companion.ime
  @Composable get() = ime
