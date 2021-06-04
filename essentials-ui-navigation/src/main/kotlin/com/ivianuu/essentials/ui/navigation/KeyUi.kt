/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

typealias KeyUi<K> = @Composable () -> Unit

typealias KeyUiFactory<K> = (K, KeyUiScope) -> KeyUi<K>

@Provide class KeyUiModule_<@Spread T : KeyUi<K>, K : Key<*>> {
  @Provide fun keyUi(
    keyClass: KClass<K>,
    keyUiFactory: (@Provide K, @Provide KeyUiScope) -> KeyUi<K>
  ): Pair<KClass<Key<*>>, KeyUiFactory<Key<*>>> =
    (keyClass to keyUiFactory).cast()

  @Provide fun keyUiOptionFactory(
    keyClass: KClass<K>,
    keyUiOptionsFactory: KeyUiOptionsFactory<K> = noOpKeyUiOptionFactory()
  ): Pair<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>> =
    (keyClass to keyUiOptionsFactory).cast()
}

typealias ModelKeyUi<K, S> = @Composable ModelKeyUiScope<K, S>.() -> Unit

@Composable operator fun <S> ModelKeyUi<*, S>.invoke(model: S) {
  invoke(
    object : ModelKeyUiScope<Nothing, S> {
      override val model: S
        get() = model
    }
  )
}

@Stable interface ModelKeyUiScope<K, S> {
  val model: S
}

@Provide fun <@Spread U : ModelKeyUi<K, S>, K : Key<*>, S> modelKeyUi(
  uiFactory: () -> U,
  model: StateFlow<S>
): KeyUi<K> = {
  val currentModel by model.collectAsState()
  val scope = remember {
    object : ModelKeyUiScope<K, S> {
      override val model: S
        get() = currentModel
    }
  }
  val ui = remember(uiFactory) as @Composable ModelKeyUiScope<K, S>.() -> Unit
  scope.ui()
}
