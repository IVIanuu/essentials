/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlin.reflect.*

fun interface KeyUi<K : Key<*>> {
  @Composable operator fun invoke()
}

typealias KeyUiFactory<K> = (K) -> KeyUi<K>

@Provide class KeyUiModule<@Spread T : KeyUi<K>, K : Key<*>> {
  @Provide fun keyUi(
    keyClass: KClass<K>,
    keyUiFactory: KeyUiFactory<K>
  ): Pair<KClass<Key<*>>, KeyUiFactory<Key<*>>> = (keyClass to keyUiFactory) as Pair<KClass<Key<*>>, KeyUiFactory<Key<*>>>

  @Provide fun keyUiOptionFactory(
    keyClass: KClass<K>,
    keyUiOptionsFactory: KeyUiOptionsFactory<K> = noOpKeyUiOptionFactory()
  ): Pair<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>> =
    (keyClass to keyUiOptionsFactory) as Pair<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>>
}

// todo make fun interface once compose is fixed
interface ModelKeyUi<K : Key<*>, S> {
  @Composable operator fun S.invoke()
}
inline operator fun <K : Key<*>, S> ModelKeyUi(
  crossinline block: @Composable S.() -> Unit
): ModelKeyUi<K, S> = object : ModelKeyUi<K, S> {
  @Composable override fun S.invoke() {
    block()
  }
}

@Provide fun <@Spread U : ModelKeyUi<K, S>, K : Key<*>, S> modelKeyUi(
  ui: U,
  model: Model<S>
): KeyUi<K> = KeyUi {
  with(ui) {
    with(model()) {
      invoke()
    }
  }
}

// todo make fun interface once compose is fixed
interface Model<out S> {
  @Composable operator fun invoke(): S
}
inline operator fun <S> Model(
  crossinline block: @Composable () -> S
): Model<S> = object : Model<S> {
  @Composable override fun invoke() = block()
}

@Provide data class KeyUiContext<K : Key<*>>(
  @Provide val key: K,
  @Provide val navigator: Navigator,
  @Provide val coroutineScope: NamedCoroutineScope<KeyUiScope>
)
