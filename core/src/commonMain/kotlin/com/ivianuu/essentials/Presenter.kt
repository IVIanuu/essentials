package com.ivianuu.essentials

import androidx.compose.runtime.*

@Stable fun interface Presenter<S> {
  @Composable fun present(): S
}
