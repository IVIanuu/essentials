package com.ivianuu.essentials.compose

import androidx.compose.ui.platform.AndroidUiDispatcher
import com.ivianuu.injekt.Provide

actual object StateCoroutineContextModule {
  @Provide actual val context: StateCoroutineContext by lazy { AndroidUiDispatcher.Main }
}
