package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.ivianuu.injekt.Provide

// todo make fun interface once compose is fixed
@Stable interface Presenter<out S> {
  @Composable operator fun invoke(): S

  @Provide companion object {
    @Provide fun unit() = Presenter {
    }
  }
}

inline fun <S> Presenter(crossinline block: @Composable () -> S): Presenter<S> = object : Presenter<S> {
  @Composable override fun invoke() = block()
}
