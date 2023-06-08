/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.NamedCoroutineScope
import com.ivianuu.injekt.common.Scope
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

// todo make fun interface once compose is fixed
interface KeyUi<K : Key<*>, M> {
  @Composable operator fun M.invoke()
}

inline fun <K : Key<*>, M> KeyUi(
  crossinline block: @Composable M.() -> Unit
): KeyUi<K, M> = object : KeyUi<K, M> {
  @Composable override fun M.invoke() {
    block()
  }
}

typealias KeyUiFactory<K> = (Navigator, Scope<KeyUiScope>, K) -> KeyUiWithModel<K>

object KeyUiModule {
  @Provide fun <@Spread T : KeyUi<K, *>, K : Key<*>> keyUi(
    keyClass: KClass<K>,
    keyUiFactory: KeyUiFactory<K>
  ): Pair<KClass<Key<*>>, KeyUiFactory<Key<*>>> =
    (keyClass to keyUiFactory) as Pair<KClass<Key<*>>, KeyUiFactory<Key<*>>>

  @Provide fun <@Spread T : KeyUi<K, *>, K : Key<*>> keyUiOptionFactory(
    keyClass: KClass<K>,
    keyUiOptionsFactory: KeyUiOptionsFactory<K> = noOpKeyUiOptionFactory()
  ): Pair<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>> =
    (keyClass to keyUiOptionsFactory) as Pair<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>>

  @Provide fun <@Spread T : KeyUi<K, *>, K : Key<*>> keyTypeKey(
    keyClass: KClass<K>,
    keyType: TypeKey<K>
  ): Pair<KClass<Key<*>>, TypeKey<Key<*>>> =
    (keyClass to keyType) as Pair<KClass<Key<*>>, TypeKey<Key<*>>>
}

interface KeyUiWithModel<K : Key<*>> {
  @Composable operator fun invoke(): @Composable () -> Unit

  companion object {
    @Provide fun <@Spread U : KeyUi<K, M>, K : Key<*>, M> impl(
      ui: U,
      model: Model<M>
    ): KeyUiWithModel<K> = object : KeyUiWithModel<K> {
      @Composable override fun invoke(): @Composable () -> Unit {
        val currentModel = model()
        return remember(currentModel) {
          {
            with(ui) {
              with(currentModel) {
                invoke()
              }
            }
          }
        }
      }
    }
  }
}

// todo make fun interface once compose is fixed
interface Model<out S> {
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
  private val coroutineScope: NamedCoroutineScope<KeyUiScope>
) : CoroutineScope by coroutineScope
