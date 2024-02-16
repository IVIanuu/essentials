package com.ivianuu.essentials.compose

import androidx.compose.runtime.Composable

@Composable fun ObserveScope(body: @Composable () -> Unit) {
  body()
}
