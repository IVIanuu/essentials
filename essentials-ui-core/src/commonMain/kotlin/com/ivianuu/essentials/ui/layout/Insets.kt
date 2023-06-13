/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.layout

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect fun Modifier.systemBarsPadding(): Modifier

expect fun Modifier.statusBarsPadding(): Modifier

expect fun Modifier.navigationBarsPadding(): Modifier

expect fun Modifier.imePadding(): Modifier

expect val WindowInsets.Companion.systemBars: WindowInsets
  @Composable get

expect val WindowInsets.Companion.statusBars: WindowInsets
  @Composable get

expect val WindowInsets.Companion.navigationBars: WindowInsets
  @Composable get

expect val WindowInsets.Companion.ime: WindowInsets
  @Composable get
