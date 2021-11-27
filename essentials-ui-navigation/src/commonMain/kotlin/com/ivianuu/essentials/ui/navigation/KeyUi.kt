/*
 * Copyright 2021 Manuel Wrage
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlin.reflect.KClass

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
  model: @Composable () -> S
) = KeyUi<K> {
  val currentModel = model()
  val scope = object : ModelKeyUiScope<K, S> {
    override val model: S
      get() = currentModel
  }
  val ui = remember(uiFactory)
  with(ui) {
    with(scope) {
      invoke()
    }
  }
}

@Provide data class KeyUiContext<K : Key<*>>(
  @Provide val key: K,
  @Provide val navigator: Navigator,
  @Provide val scope: NamedCoroutineScope<KeyUiScope>
)
