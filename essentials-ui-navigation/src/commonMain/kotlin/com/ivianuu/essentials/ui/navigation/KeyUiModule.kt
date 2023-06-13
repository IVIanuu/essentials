/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.unsafeCast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import kotlin.reflect.KClass

typealias KeyUiFactory<K> = (Navigator, Scope<KeyUiScope>, K) -> Ui<K, *>

object KeyUiModule {
  @Provide fun <@Spread T : Ui<K, *>, K : Key<*>> ui(
    keyClass: KClass<K>,
    keyUiFactory: KeyUiFactory<K>
  ): Pair<KClass<Key<*>>, KeyUiFactory<Key<*>>> =
    (keyClass to keyUiFactory).unsafeCast()

  @Provide fun <@Spread T : Ui<K, M>, K : Key<*>, M> model(
    keyClass: KClass<K>,
    modelFactory: ModelFactory<K, M>
  ): Pair<KClass<Key<*>>, ModelFactory<*, *>> =
    (keyClass to modelFactory).unsafeCast()

  @Provide fun <@Spread T : Ui<K, *>, K : Key<*>> config(
    keyClass: KClass<K>,
    keyConfigFactory: KeyConfigFactory<K> = { _, _, _ -> KeyConfig() }
  ): Pair<KClass<Key<*>>, KeyConfigFactory<Key<*>>> =
    (keyClass to keyConfigFactory).unsafeCast()
}

typealias ModelFactory<K, S> = (Navigator, Scope<KeyUiScope>, K) -> Model<S>

typealias KeyConfigFactory<K> = (Navigator, Scope<KeyUiScope>, K) -> KeyConfig<K>
