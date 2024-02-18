package com.ivianuu.essentials

import androidx.compose.runtime.*

fun interface Presenter<S> {
  @Composable fun present(): S
}
