package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import com.ivianuu.injekt.*

@Stable fun interface Presenter<out S> {
  @Composable operator fun invoke(): S

  @Provide companion object {
    @Provide fun unit() = Presenter {
    }
  }
}
