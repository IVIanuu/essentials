/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.state.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlin.reflect.*

fun interface KeyUi<K : Key<*>> : @Composable () -> Unit

typealias KeyUiFactory<K> = (K) -> KeyUi<K>

@Provide class KeyUiModule<@Spread T : KeyUi<K>, K : Key<*>> {
  @Provide fun keyUi(
    keyClass: KClass<K>,
    keyUiFactory: KeyUiFactory<K>
  ): Pair<KClass<Key<*>>, KeyUiFactory<Key<*>>> = (keyClass to keyUiFactory).cast()

  @Provide fun keyUiOptionFactory(
    keyClass: KClass<K>,
    keyUiOptionsFactory: KeyUiOptionsFactory<K> = noOpKeyUiOptionFactory()
  ): Pair<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>> =
    (keyClass to keyUiOptionsFactory).cast()
}

// todo make fun interface once compose is fixed
interface ModelKeyUi<K : Key<*>, S> {
  @Composable operator fun ModelKeyUiScope<K, S>.invoke()
}
inline operator fun <K : Key<*>, S> ModelKeyUi(
  crossinline block: @Composable ModelKeyUiScope<K, S>.() -> Unit
) = object : ModelKeyUi<K, S> {
  @Composable override fun ModelKeyUiScope<K, S>.invoke() {
    block()
  }
}

@Composable operator fun <K : Key<*>, S> ModelKeyUi<K, S>.invoke(model: S) {
  with(
    object : ModelKeyUiScope<K, S> {
      override val model: S
        get() = model
    }
  ) {
    invoke()
  }
}

@Stable interface ModelKeyUiScope<K : Key<*>, S> {
  val model: S
}

@Provide fun <@Spread U : ModelKeyUi<K, S>, K : Key<*>, S> modelKeyUi(
  uiFactory: () -> U,
  model: (StateScope) -> S,
  coroutineScope: NamedCoroutineScope<KeyUiScope>,
  scope: Scope<KeyUiScope>,
  sKey: TypeKey<S>
) = KeyUi<K> {
  val currentModel by scope { state(block = model, scope = coroutineScope) }.collectAsState()
  val uiScope = object : ModelKeyUiScope<K, S> {
    override val model: S
      get() = currentModel
  }
  val ui = remember(uiFactory)
  with(ui) {
    with(uiScope) {
      invoke()
    }
  }
}

@Provide data class KeyUiContext<K : Key<*>>(
  @Provide val key: K,
  @Provide val navigator: Navigator,
  @Provide val scope: NamedCoroutineScope<KeyUiScope>,
  @Provide val stateScope: StateScope
)
