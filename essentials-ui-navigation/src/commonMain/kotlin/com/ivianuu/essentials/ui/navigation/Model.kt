package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.ivianuu.injekt.Provide

// todo make fun interface once compose is fixed
@Stable interface Model<out S> {
  @Composable operator fun invoke(): S

  companion object {
    @Provide fun unit() = Model {
    }
  }
}

inline fun <S> Model(crossinline block: @Composable () -> S): Model<S> = object : Model<S> {
  @Composable override fun invoke() = block()
}
