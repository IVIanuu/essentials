/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.injekt.*
import kotlin.reflect.*

@Stable interface Screen<T>

interface RootScreen : Screen<Unit>

interface OverlayScreen<T> : Screen<T> {
  @Provide companion object {
    @Provide fun <T : OverlayScreen<*>> screenConfig() =
      ScreenConfig<T>(opaque = true, transitionSpec = {
        if (isPush)
          ContentKey entersWith fadeIn()
        else
          ContentKey exitsWith fadeOut()
      })
  }
}

interface CriticalUserFlowScreen<T> : Screen<T>

val Scope<*>.screen: Screen<*> get() = service()

data class ScreenConfig<T : Screen<*>>(
  val enterTransitionSpec: (ElementTransitionSpec<Screen<*>>)? = null,
  val exitTransitionSpec: (ElementTransitionSpec<Screen<*>>)? = null,
  val opaque: Boolean = false,
) {
  constructor(
    opaque: Boolean = false,
    transitionSpec: ElementTransitionSpec<Screen<*>>
  ) : this(transitionSpec, transitionSpec, opaque)
}

class ScreenScope {
  override fun toString() = "ScreenScope"
}

fun interface ScreenDecorator : ExtensionPoint<ScreenDecorator> {
  @Composable fun DecoratedContent(content: @Composable () -> Unit)
}

@Provide class DecorateScreen(
  private val records: List<ExtensionPointRecord<ScreenDecorator>>,
  private val logger: Logger
) {
  @Composable fun DecoratedContent(content: @Composable () -> Unit) {
    val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(records) {
      records
        .sortedWithLoadingOrder()
        .fold({ it() }) { acc, record ->
          { content ->
            acc {
              logger.d { "decorate screen with ${record.key.qualifiedName}" }
              record.instance.DecoratedContent(content)
            }
          }
        }
    }

    logger.d { "decorate screen $content with combined $combinedDecorator" }

    combinedDecorator(content)
  }
}

@Provide object ScreenModule {
  @Provide fun <@AddOn T : Ui<S>, S : Screen<*>> rootNavGraphUiFactory(
    screenClass: KClass<S>,
    uiFactory: UiFactory<S>
  ): Pair<KClass<Screen<*>>, @NavGraph<RootNavGraph> UiFactory<Screen<*>>> =
    (screenClass to uiFactory).unsafeCast()

  @Provide fun <@AddOn T : Ui<S>, S : Screen<*>> rootNavGraphConfigFactory(
    screenClass: KClass<S>,
    screenConfigFactory: ScreenConfigFactory<S> = { _, _, _ -> ScreenConfig() }
  ): Pair<KClass<Screen<*>>, @NavGraph<RootNavGraph> ScreenConfigFactory<Screen<*>>> =
    (screenClass to screenConfigFactory).unsafeCast()

  @Provide fun <@AddOn T : @NavGraph<N> Ui<S>, N, S : Screen<*>> navGraphUiFactory(
    screenClass: KClass<S>,
    uiFactory: (Navigator, Scope<ScreenScope>, S) -> @NavGraph<N> Ui<S>
  ): Pair<KClass<Screen<*>>, @NavGraph<N> UiFactory<Screen<*>>> =
    (screenClass to uiFactory).unsafeCast()

  @Provide fun <@AddOn T : @NavGraph<N> Ui<S>, N, S : Screen<*>> navGraphConfigFactory(
    screenClass: KClass<S>,
    screenConfigFactory: ScreenConfigFactory<S> = { _, _, _ -> ScreenConfig() }
  ): Pair<KClass<Screen<*>>, @NavGraph<N> ScreenConfigFactory<Screen<*>>> =
    (screenClass to screenConfigFactory).unsafeCast()
}

typealias UiFactory<S> = (Navigator, Scope<ScreenScope>, S) -> Ui<S>

typealias ScreenConfigFactory<S> = (Navigator, Scope<ScreenScope>, S) -> ScreenConfig<S>
