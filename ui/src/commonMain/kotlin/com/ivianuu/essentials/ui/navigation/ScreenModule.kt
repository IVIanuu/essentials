/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import kotlin.reflect.*

@Provide object ScreenModule {
  @Provide fun <@Spread T : Ui<S, *>, S : Screen<*>> rootNavGraphUiFactory(
    screenClass: KClass<S>,
    uiFactory: UiFactory<S>
  ): Pair<KClass<Screen<*>>, @NavGraph<RootNavGraph> UiFactory<Screen<*>>> =
    (screenClass to uiFactory).unsafeCast()

  @Provide fun <@Spread T : Ui<S, M>, S : Screen<*>, M> rootNavGraphPresenterFactory(
    screenClass: KClass<S>,
    presenterFactory: PresenterFactory<S, M>
  ): Pair<KClass<Screen<*>>, @NavGraph<RootNavGraph> PresenterFactory<Screen<*>, *>> =
    (screenClass to presenterFactory).unsafeCast()

  @Provide fun <@Spread T : Ui<S, *>, S : Screen<*>> rootNavGraphConfigFactory(
    screenClass: KClass<S>,
    screenConfigFactory: ScreenConfigFactory<S> = { _, _, _ -> ScreenConfig() }
  ): Pair<KClass<Screen<*>>, @NavGraph<RootNavGraph> ScreenConfigFactory<Screen<*>>> =
    (screenClass to screenConfigFactory).unsafeCast()

  @Provide fun <@Spread T : @NavGraph<N> Ui<S, *>, N, S : Screen<*>> navGraphUiFactory(
    screenClass: KClass<S>,
    uiFactory: (Navigator, Scope<ScreenScope>, S) -> @NavGraph<N> Ui<S, *>
  ): Pair<KClass<Screen<*>>, @NavGraph<N> UiFactory<Screen<*>>> =
    (screenClass to uiFactory).unsafeCast()

  @Provide fun <@Spread T : @NavGraph<N> Ui<S, M>, N, S : Screen<*>, M> navGraphPresenterFactory(
    screenClass: KClass<S>,
    presenterFactory: PresenterFactory<S, M>
  ): Pair<KClass<Screen<*>>, @NavGraph<N> PresenterFactory<Screen<*>, *>> =
    (screenClass to presenterFactory).unsafeCast()

  @Provide fun <@Spread T : @NavGraph<N> Ui<S, *>, N, S : Screen<*>> navGraphConfigFactory(
    screenClass: KClass<S>,
    screenConfigFactory: ScreenConfigFactory<S> = { _, _, _ -> ScreenConfig() }
  ): Pair<KClass<Screen<*>>, @NavGraph<N> ScreenConfigFactory<Screen<*>>> =
    (screenClass to screenConfigFactory).unsafeCast()
}

typealias UiFactory<S> = (Navigator, Scope<ScreenScope>, S) -> Ui<S, *>

typealias PresenterFactory<S, M> = (Navigator, Scope<ScreenScope>, S) -> Presenter<M>

typealias ScreenConfigFactory<S> = (Navigator, Scope<ScreenScope>, S) -> ScreenConfig<S>
