/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.Scope
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

fun interface KeyUi<K : Key<*>> {
  @Composable operator fun invoke(): @Composable () -> Unit
}

fun <K: Key<*>> SimpleKeyUi(block: @Composable () -> Unit) = KeyUi<K> { block }

typealias KeyUiFactory<K> = (Scope<KeyUiScope>, K) -> KeyUi<K>

object KeyUiModule {
  @Provide fun <@Spread T : KeyUi<K>, K : Key<*>> keyUi(
    keyClass: KClass<K>,
    keyUiFactory: KeyUiFactory<K>
  ): Pair<KClass<Key<*>>, KeyUiFactory<Key<*>>> =
    (keyClass to keyUiFactory) as Pair<KClass<Key<*>>, KeyUiFactory<Key<*>>>

  @Provide fun <@Spread T : KeyUi<K>, K : Key<*>> keyUiOptionFactory(
    keyClass: KClass<K>,
    keyUiOptionsFactory: KeyUiOptionsFactory<K> = noOpKeyUiOptionFactory()
  ): Pair<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>> =
    (keyClass to keyUiOptionsFactory) as Pair<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>>

  @Provide fun <@Spread T : KeyUi<K>, K : Key<*>> keyTypeKey(
    keyClass: KClass<K>,
    keyType: TypeKey<K>
  ): Pair<KClass<Key<*>>, TypeKey<Key<*>>> =
    (keyClass to keyType) as Pair<KClass<Key<*>>, TypeKey<Key<*>>>
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
  val currentModel = model()
  remember(currentModel) {
    {
      with(ui) {
        with(currentModel) {
          invoke()
        }
      }
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
  private val coroutineScope: NamedCoroutineScope<KeyUiScope>
) : CoroutineScope by coroutineScope
