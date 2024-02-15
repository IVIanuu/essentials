package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.ivianuu.injekt.Provide

@Stable fun interface Presenter<out S> {
  @Composable operator fun invoke(): S

  @Provide companion object {
    @Provide fun unit() = Presenter {
    }
  }
}
