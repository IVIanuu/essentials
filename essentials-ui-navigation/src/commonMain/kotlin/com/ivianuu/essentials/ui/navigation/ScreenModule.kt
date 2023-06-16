/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.Service
import com.ivianuu.essentials.unsafeCast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import kotlin.reflect.KClass

object ScreenModule {
  @Provide fun <@Spread T : Ui<S, *>, S : Screen<*>> ui(
    screenClass: KClass<S>,
    uiFactory: UiFactory<S>
  ): Pair<KClass<Screen<*>>, UiFactory<Screen<*>>> =
    (screenClass to uiFactory).unsafeCast()

  @Provide fun <@Spread T : Ui<S, M>, S : Screen<*>, M> model(
    screenClass: KClass<S>,
    modelFactory: ModelFactory<S, M>
  ): Pair<KClass<Screen<*>>, ModelFactory<*, *>> =
    (screenClass to modelFactory).unsafeCast()

  @Provide fun <@Spread T : Ui<S, *>, S : Screen<*>> config(
    screenClass: KClass<S>,
    screenConfigFactory: ScreenConfigFactory<S> = { _, _, _ -> ScreenConfig() }
  ): Pair<KClass<Screen<*>>, ScreenConfigFactory<Screen<*>>> =
    (screenClass to screenConfigFactory).unsafeCast()

  @Provide fun <@Spread T : Ui<S, *>, S : Screen<*>> scope(
    screenClass: KClass<S>,
    scopeFactory: ScreenScopeFactory<S>
  ): Pair<KClass<Screen<*>>, ScreenScopeFactory<Screen<*>>> =
    (screenClass to scopeFactory).unsafeCast()

  @Provide fun <@Spread T : Ui<S, *>, S : Screen<*>> screenService(screen: S):
      @Service<ScreenScope<S>> Screen<*> = screen
}

typealias UiFactory<S> = (Navigator, Scope<ScreenScope<*>>, S) -> Ui<S, *>

typealias ModelFactory<S, M> = (Navigator, Scope<ScreenScope<*>>, S) -> Model<M>

typealias ScreenConfigFactory<S> = (Navigator, Scope<ScreenScope<*>>, S) -> ScreenConfig<S>

typealias ScreenScopeFactory<S> = (Navigator, S) -> Scope<ScreenScope<S>>
