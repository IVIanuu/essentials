package com.ivianuu.essentials.compose

import androidx.compose.runtime.*

@Composable fun ObserveScope(body: @Composable () -> Unit) {
  body()
}
