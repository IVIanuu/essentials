package com.ivianuu.essentials.compose

import com.ivianuu.injekt.Provide
import kotlin.coroutines.CoroutineContext

@Provide val androidUiDispatcher: @AndroidUiDispatcher CoroutineContext =
  androidx.compose.ui.platform.AndroidUiDispatcher.Main
