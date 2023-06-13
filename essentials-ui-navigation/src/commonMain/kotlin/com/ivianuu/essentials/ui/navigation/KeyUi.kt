/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CoroutineScope

// todo make fun interface once compose is fixed
@Stable interface KeyUi<K : Key<*>, M> {
  @Composable operator fun invoke(model: M)
}

inline fun <K : Key<*>, M> KeyUi(
  crossinline block: @Composable (M) -> Unit
): KeyUi<K, M> = object : KeyUi<K, M> {
  @Composable override fun invoke(model: M) {
    block(model)
  }
}

// todo make fun interface once compose is fixed
@Stable interface Model<out S> {
  @Composable operator fun invoke(): S

  companion object {
    @Provide fun unit() = Model {
    }
  }
}

inline fun <S> Model(
  crossinline block: @Composable () -> S
): Model<S> = object : Model<S> {
  @Composable override fun invoke() = block()
}

@Provide data class KeyUiContext<K : Key<*>>(
  val key: K,
  val navigator: Navigator,
  private val coroutineScope: ScopedCoroutineScope<KeyUiScope>
) : CoroutineScope by coroutineScope
