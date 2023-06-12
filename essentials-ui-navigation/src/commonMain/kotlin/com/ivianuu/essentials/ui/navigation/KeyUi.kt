/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.unsafeCast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

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

typealias KeyUiFactory<K> = (Navigator, Scope<KeyUiScope>, K) -> KeyUi<K, *>

object KeyUiModule {
  @Provide fun <@Spread T : KeyUi<K, *>, K : Key<*>> keyUi(
    keyClass: KClass<K>,
    keyUiFactory: KeyUiFactory<K>
  ): Pair<KClass<Key<*>>, KeyUiFactory<Key<*>>> =
    (keyClass to keyUiFactory).unsafeCast()

  @Provide fun <@Spread T : KeyUi<K, M>, K : Key<*>, M> model(
    keyClass: KClass<K>,
    modelFactory: ModelFactory<K, M>
  ): Pair<KClass<Key<*>>, ModelFactory<*, *>> =
    (keyClass to modelFactory).unsafeCast()

  @Provide fun <@Spread T : KeyUi<K, *>, K : Key<*>> optionsFactory(
    keyClass: KClass<K>,
    keyUiOptionsFactory: KeyUiOptionsFactory<K> = noOpKeyUiOptionFactory()
  ): Pair<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>> =
    (keyClass to keyUiOptionsFactory).unsafeCast()

  @Provide fun <@Spread T : KeyUi<K, *>, K : Key<*>> typeKey(
    keyClass: KClass<K>,
    keyType: TypeKey<K>
  ): Pair<KClass<Key<*>>, TypeKey<Key<*>>> =
    (keyClass to keyType).unsafeCast()
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

typealias ModelFactory<K, S> = (Navigator, Scope<KeyUiScope>, K) -> Model<S>

@Provide data class KeyUiContext<K : Key<*>>(
  val key: K,
  val navigator: Navigator,
  private val coroutineScope: ScopedCoroutineScope<KeyUiScope>
) : CoroutineScope by coroutineScope
