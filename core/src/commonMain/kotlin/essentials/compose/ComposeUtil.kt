package essentials.compose

import androidx.compose.runtime.*

@Composable fun RestartableScope(block: @Composable () -> Unit) {
  block()
}
